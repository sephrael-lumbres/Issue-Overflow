package com.sephrael.issueoverflow.service;

import com.sephrael.issueoverflow.entity.File;
import com.sephrael.issueoverflow.entity.Issue;
import com.sephrael.issueoverflow.entity.User;
import com.sephrael.issueoverflow.repository.FileRepository;
import com.sephrael.issueoverflow.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserRepository userRepository;

    private static final Logger log = getLogger(UserService.class);

    public void uploadFile(User user, MultipartFile file, boolean isProfilePicture) {
        try {
            if(user.hasProfilePicture() && isProfilePicture) {
                deleteFile(fileRepository.findByUserAndIsProfilePicture(user, true).getId());
            }
            if(!user.hasProfilePicture() && isProfilePicture) {
                user.setHasProfilePicture(true);
                userRepository.save(user);
            }

            store(file, user, isProfilePicture);
        } catch(IOException ioException) {
            //TODO exception handling please?
            log.error("Saving file to db failed: " + ioException);
            ioException.printStackTrace();
        }
    }

    public File store(MultipartFile multipartFile, User user, boolean isProfilePicture) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        File file = new File(fileName, multipartFile.getContentType(), multipartFile.getSize(), isProfilePicture, multipartFile.getBytes());
        user.addToFiles(file);

        return fileRepository.save(file);
    }

    public void uploadIssueAttachments(User user, MultipartFile file, boolean isProfilePicture, Issue issue) {
        try {
            storeIssueAttachments(file, user, isProfilePicture, issue);
        } catch(IOException ioException) {
            //TODO exception handling please?
            log.error("Saving file to db failed: " + ioException);
            ioException.printStackTrace();
        }
    }

    public void storeIssueAttachments(MultipartFile multipartFile, User user, boolean isProfilePicture, Issue issue) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        File file = new File(fileName, multipartFile.getContentType(), multipartFile.getSize(), isProfilePicture, multipartFile.getBytes());
        user.addToFiles(file);
        issue.addToFiles(file);

        fileRepository.save(file);
    }

    public File getFile(Long id) {
        return fileRepository.findById(id).get();
    }

    public Stream<File> getAllFiles() {
        return fileRepository.findAll().stream();
    }

    public void deleteFile(long id) {
        fileRepository.deleteById(id);
    }
}
