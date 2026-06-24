package com.example.expensestracker.file.api;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.JwtAuthorizationFilter;
import com.example.expensestracker.authorization.WebSecurityConfiguration;
import com.example.expensestracker.authorization.api.MyUserDetailsService;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.file.api.dto.FileDataDTO;
import com.example.expensestracker.file.service.FileService;
import com.example.expensestracker.file.service.impl.FileServiceImpl;
import com.example.expensestracker.file.service.model.FileData;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.general.exception.ErrorStrategy;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.UserService;
import com.example.expensestracker.user.api.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = FileStorageController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FileServiceImpl.class),
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                ErrorStrategy.class, WebSecurityConfiguration.class, MyUserDetailsService.class,
                JwtAuthorizationFilter.class, JwtService.class}))
class FileStorageControllerTest {
    private static final String API_URL = "/api/files";
    private static final String USER_EMAIL = "test@email.com";
    private static final String FOLDER_PATH = "/Example/folder/path/";
    private static final Long TRANSACTION_ID = 1L;
    private static final Long FILE_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final FinancialTransaction TRANSACTION = TestUtils.createTransactionForTest(TRANSACTION_ID, EXPENSE);
    private static final MockMultipartFile MOCK_MULTIPART = new MockMultipartFile(
            "file", "document.pdf", MediaType.APPLICATION_PDF_VALUE, "test_content".getBytes());
    private static final User MOCK_USER = TestUtils.createUser(USER_EMAIL);

    @MockBean
    private FileService fileService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = USER_EMAIL)
    void testUploadFileForTransaction() throws Exception {
        FileData fileData = new FileData(
                FILE_ID, MOCK_MULTIPART.getOriginalFilename(), MOCK_MULTIPART.getContentType(),
                FOLDER_PATH + MOCK_MULTIPART.getOriginalFilename(), TRANSACTION);
        FileDataDTO expectedDTO = new FileDataDTO(fileData.getFileName(), fileData.getType(), TRANSACTION_ID);

        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(MOCK_USER);
        when(fileService.uploadFileForTransaction(MOCK_MULTIPART, TRANSACTION_ID, USER_ID)).thenReturn(expectedDTO);

        // when and then
        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL + "/" + TRANSACTION_ID)
                        .file(MOCK_MULTIPART)
                        .contentType(MediaType.APPLICATION_PDF_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedDTO)));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void testUploadFileForTransaction_transactionNotExists_throwAppRuntimeException() throws Exception {
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(MOCK_USER);
        doThrow(new AppRuntimeException(ErrorCode.FT001, "Transaction not found"))
                .when(fileService).uploadFileForTransaction(MOCK_MULTIPART, TRANSACTION_ID, USER_ID);

        // when and then
        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL + "/" + TRANSACTION_ID)
                        .file(MOCK_MULTIPART)
                        .contentType(MediaType.APPLICATION_PDF_VALUE))
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.businessMessage").value(ErrorCode.FT001.getBusinessMessage()),
                        MockMvcResultMatchers.jsonPath("$.businessCode").value(ErrorCode.FT001.getBusinessCode()),
                        MockMvcResultMatchers.jsonPath("$.description").value("Transaction not found"),
                        MockMvcResultMatchers.jsonPath("$.statusCode").value(ErrorCode.FT001.getHttpStatus()));
    }


}