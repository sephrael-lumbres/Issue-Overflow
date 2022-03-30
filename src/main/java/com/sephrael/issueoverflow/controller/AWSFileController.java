package com.sephrael.issueoverflow.controller;

import com.sephrael.issueoverflow.entity.AWSFile;
import com.sephrael.issueoverflow.entity.User;
import com.sephrael.issueoverflow.repository.AWSFileRepository;
import com.sephrael.issueoverflow.repository.UserRepository;
import com.sephrael.issueoverflow.service.AWSFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.Objects;

@Controller
@RequestMapping("/storage")
public class AWSFileController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AWSFileService awsFileService;

    @Autowired
    private AWSFileRepository awsFileRepository;

    @PostMapping("/upload/{userId}/{isProfilePicture}")
    public String uploadFile(@PathVariable("userId") long userId, @RequestParam("file")MultipartFile file, Principal principal,
                             @PathVariable("isProfilePicture") boolean isProfilePicture, RedirectAttributes redirectAttributes) {
        User user = userRepository.findUserById(userId);

        // if Current User does NOT match the requested User, redirect to 404 page
        if(userRepository.findByEmail(principal.getName()).getId() != userId)
            return "error/404";

        // if 'File' input is NOT empty and the 'User' already has a 'Profile Picture', DELETE the User's current
        // 'Profile Picture', then upload the new 'Profile Picture'
        if(!file.isEmpty() && user.hasProfilePicture()) {
            awsFileService.deleteFile(awsFileRepository.findByUserAndIsProfilePictureTrue(user).getFileKey());
            user.setHasProfilePicture(false);
            awsFileService.checkUserProfilePicture(user, file, isProfilePicture);
            redirectAttributes.addFlashAttribute("pictureUploadSuccess", "Profile Picture has been successfully uploaded");

            // if the 'User' uploaded an empty 'File' input AND the 'User' already has a 'Profile Picture', DELETE the
            // User's current 'Profile Picture' and SET the User's 'hasProfilePicture' to FALSE
        } else if(file.isEmpty() && user.hasProfilePicture()) {
            awsFileService.deleteFile(awsFileRepository.findByUserAndIsProfilePictureTrue(user).getFileKey());

            user.setHasProfilePicture(false);
            userRepository.save(user);

            // if 'File' input is empty and 'User' does NOT have a 'Profile Picture', redirect to 'Account Profile' page
        } else if(file.isEmpty() && !user.hasProfilePicture()) {
            return "redirect:/account/profile/" + userId;

            // if 'File' input is NOT empty AND 'User' does NOT have a 'Profile Picture', upload and set the 'File' to
            // the User's 'Profile Picture'
        } else if(!file.isEmpty() && !user.hasProfilePicture()) {
            awsFileService.checkUserProfilePicture(user, file, isProfilePicture);
            redirectAttributes.addFlashAttribute("pictureUploadSuccess", "Profile Picture has been successfully uploaded");
        } else
            redirectAttributes.addFlashAttribute("pictureUploadError", "An error has occurred while attempting to upload Profile Picture");;

        return "redirect:/account/profile/" + userId;
    }

    @GetMapping("/download/{fileKey}")
    public ResponseEntity<byte[]> downloadFileByKey(@PathVariable String fileKey, Principal principal) throws IOException {
        User currentUser = userRepository.findByEmail(principal.getName());
        AWSFile awsFile = awsFileRepository.findByFileKey(fileKey);
        byte[] AWSFileData = awsFileService.getAWSFile(fileKey);

        // if the requested file's Organization does NOT match the current User's Organization, redirect to NOT FOUND page
        if(!Objects.equals(awsFile.getUser().getOrganization().getId(), currentUser.getOrganization().getId()))
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentLength(AWSFileData.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + awsFile.getFileName() + "\"")
                .body(AWSFileData);
    }

    @GetMapping("/profile-picture/{userId}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable("userId") long userId, Principal principal) throws IOException {
        // if the requested File's 'Organization' does NOT match the Current User's 'Organization', redirect to NOT FOUND page
        if(userRepository.findUserById(userId).getOrganization() != userRepository.findByEmail(principal.getName()).getOrganization())
            return ResponseEntity.notFound().build();

        AWSFile awsFile = awsFileRepository.findByUserAndIsProfilePictureTrue(userRepository.findUserById(userId));
        byte[] AWSFileData = awsFileService.getAWSFile(awsFile.getFileKey());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + awsFile.getFileName() + "\"")
                .body(AWSFileData);
    }

    @RequestMapping("/{identifier}/{issueKey}/delete/{fileKey}")
    public String deleteAttachment(@PathVariable("identifier") String identifier, @PathVariable("issueKey") String issueKey,
                                   @PathVariable String fileKey, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName());
        AWSFile awsFile = awsFileRepository.findByFileKey(fileKey);

        if(currentUser.getOrganization() == null)
            return "organization/select-organization";

        if(!Objects.equals(awsFile.getUser().getId(), currentUser.getId()))
            return "error/404";

        awsFileService.deleteFile(fileKey);

        return String.format("redirect:/issues/%s/view/%s", identifier, issueKey);
    }
}
