package com.example.expensestracker.file.api;

import com.example.expensestracker.file.api.dto.FileDataDTO;
import com.example.expensestracker.file.service.FileStorageService;
import com.example.expensestracker.user.api.UserService;
import com.example.expensestracker.user.api.model.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/files")
public class FileStorageController {
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private UserService userService;

    @PostMapping("/{transactionId}")
    public ResponseEntity<FileDataDTO> uploadFileForTransaction(
            @RequestParam("file") MultipartFile file, @NotNull @PathVariable Long transactionId, Principal principal) throws IOException {
        Long userId = getPrincipalId(principal);
        FileDataDTO fileData = fileStorageService.uploadFileForTransaction(file, transactionId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(fileData);
    }

    private Long getPrincipalId(Principal principal) {
        String userEmail = principal.getName();
        User userPrincipal = userService.findUserByEmail(userEmail);
        return userPrincipal.getId();
    }

}
