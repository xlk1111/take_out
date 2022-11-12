package com.xlk.takeout.controller;


import com.xlk.takeout.common.R;
import com.xlk.takeout.entity.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

//@RestController
//@RequestMapping("/user")
//@Slf4j
public class SmtpController{

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) throws MessagingException {
        String phone = user.getPhone();
//        log.info(user.toString());

//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        SimpleMailMessage messageHelper = new SimpleMailMessage();
//        messageHelper.setFrom("设置发件qq邮箱");
        messageHelper.setTo("2691473692@qq.com");//这边填的是发送发的地址，需要写全地址
        messageHelper.setFrom("2691473692@qq.com");//这边我填的是qq邮箱地址
        messageHelper.setSubject("验证码");//邮件名字
        String code = "1234";//使用方法生成一个验证码
        messageHelper.setText("尊敬的用户,您好:\n本次请求的邮件验证码为:" + code);
//            mailSender.send(mimeMessage);
        return R.success("发送成功");
    }


}
