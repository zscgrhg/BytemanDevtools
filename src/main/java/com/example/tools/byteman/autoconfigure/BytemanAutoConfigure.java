package com.example.tools.byteman.autoconfigure;

import com.example.tools.byteman.beans.BytemanInstaller;
import com.example.tools.byteman.beans.BytemanProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({org.jboss.byteman.agent.install.Install.class, org.jboss.byteman.agent.submit.Submit.class})
@EnableConfigurationProperties(BytemanProperties.class)
public class BytemanAutoConfigure {


    @Bean
    public CommandLineRunner bytemanInstaller(@Autowired BytemanProperties props) {
        return new BytemanInstaller(props);
    }
}
