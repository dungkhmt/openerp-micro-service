package com.hust.baseweb.applications.mail.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
/**
 * @author Le Anh Tuan
 */
public class MailController {

//    MailService mailService;
//
//    @GetMapping("/simple-mail")
//    void send(@RequestBody String[] to) {
//        //void send(){
//        mailService.sendSimpleMail(to, null, null, "test", "test from openerp", null);
//        //mailService.sendSimpleMail(new String[] {"dungkhmt@gmail.com"}, null, null, "test", "test from openerp", null);
//    }
//
//    @PostMapping("/send-mail-with-multiple-files")
//    public void sendMailWithMultipleFiles(@RequestParam("files") MultipartFile[] files) {
////        String[] to = {"inflict0126104@gmail.com"};
//        String[] to = {"phamducdat2402@ gmail.com"};
//        String body = "body";
//        mailService.sendMailWithAttachments(to, null, null, "subject", body, false, null, null, files);
//    }
}
