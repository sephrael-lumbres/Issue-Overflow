package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.File;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.message.ResponseFile;
import com.sephrael.issuetrackingsystem.repository.FileRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import com.sephrael.issuetrackingsystem.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFileById(@PathVariable long id) {
        File file = fileService.getFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(file.getData());
    }

    @GetMapping("/{userId}/{isProfilePicture}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable("userId") long userId,
                                                    @PathVariable("isProfilePicture") boolean isProfilePicture) {
        File file = fileRepository.findByUserAndIsProfilePicture(userRepository.findUserById(userId), isProfilePicture);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(file.getData());
    }

    @PostMapping("/upload/{userId}/{isProfilePicture}")
    public String uploadFile(@PathVariable("userId") long userId, @RequestParam("file")MultipartFile file,
                                                      @PathVariable("isProfilePicture") boolean isProfilePicture) {
        User user = userRepository.findUserById(userId);

        if(file.isEmpty() && user.hasProfilePicture()) {
            fileService.deleteFile(fileRepository.findByUserAndIsProfilePicture(user, isProfilePicture).getId());

            user.setHasProfilePicture(false);
            userRepository.save(user);
        } else if(file.isEmpty() && !user.hasProfilePicture()) {
            return "redirect:/account/profile/" + userId;
        } else
            fileService.uploadFile(user, file, isProfilePicture);

        return "redirect:/account/profile/" + userId;
    }

    @RequestMapping("/{identifier}/{issueKey}/delete/{id}")
    public String deleteFile(@PathVariable("id") long id, @PathVariable("identifier") String identifier,
                             @PathVariable("issueKey") String issueKey, Principal principal) {
        fileRepository.deleteById(id);

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return String.format("redirect:/issues/%s/view/%s", identifier, issueKey);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResponseFile>> getAllFilesJson() {
        List<ResponseFile> files = fileService.getAllFiles().map(file -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files/")
                    .path(file.getId().toString())
                    .toUriString();

            return new ResponseFile(
                    file.getName(),
                    fileDownloadUri,
                    file.getType(),
                    file.getData().length);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }
}
