package io.infinite.orbit

import groovy.swing.SwingBuilder
import groovy.util.logging.Slf4j
import io.infinite.ascend.common.entities.Authorization
import io.infinite.ascend.granting.client.authentication.ClientJwtPreparator
import io.infinite.ascend.granting.client.services.ClientAuthorizationGrantingService
import io.infinite.blackbox.BlackBox
import io.infinite.carburetor.CarburetorLevel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

@BlackBox(level = CarburetorLevel.METHOD)
@Slf4j
@SpringBootApplication
@ComponentScan(
        basePackages = [
                "io.infinite.ascend.common",
                "io.infinite.ascend.granting",
                "io.infinite.orbit"
        ],
        excludeFilters = [
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ClientJwtPreparator.class)
        ])
@EnableJpaRepositories([
        "io.infinite.ascend",
        "io.infinite.orbit"
])
@EntityScan([
        "io.infinite.ascend",
        "io.infinite.orbit"
])
class OrbitGuiApp implements ApplicationRunner {

    String ascendClientPublicKeyName

    @Value('${ascendGrantingUrl}')
    String ascendGrantingUrl

    String scopeName

    String authorizationServerNamespace

    String authorizationClientNamespace

    @Autowired
    ClientAuthorizationGrantingService clientAuthorizationGrantingService

    Authorization adminScopeAuthorization

    SwingBuilder swing = new SwingBuilder()

    RootPaneContainer frame

    JPanel adminPanel = new JPanel()

    JPanel anonymousPanel = new JPanel().add(new JLabel("Please wait while we log you in...")).parent as JPanel

    JPanel mainPanel = new JPanel().add(anonymousPanel).parent as JPanel

    JFrame mainFrame = new JFrame(
            name: "mainFrame",
            title: "Infinite Technology ∞ Orbit Admin",
            visible: true,
            defaultCloseOperation: JFrame.EXIT_ON_CLOSE,
            size: new Dimension(1366, 768),
            locationRelativeTo: null,
            contentPane: mainPanel
    )

    JPanel unauthorizedPanel = new JPanel()

    Timer authorizationTimer = new Timer(1000, new ActionListener() {
        void actionPerformed(ActionEvent actionEvent) {
            authorized()
        }
    })

    static void main(String[] args) {
        System.setProperty("jwtAccessKeyPublic", "")
        System.setProperty("jwtAccessKeyPrivate", "")
        System.setProperty("jwtRefreshKeyPublic", "")
        System.setProperty("jwtRefreshKeyPrivate", "")
        System.setProperty("ascendValidationUrl", "")
        System.setProperty("orbitUrl", "")
        SpringApplicationBuilder builder = new SpringApplicationBuilder(OrbitGuiApp.class)
        builder.headless(false)
        ConfigurableApplicationContext context = builder.run(args)
    }

    void authorized() {
        Thread.start {
            try {
                adminScopeAuthorization = clientAuthorizationGrantingService.grantByScope("adminScope", ascendGrantingUrl, "global", "OrbitSaaS")
                showPanel(adminPanel)
                authorizationTimer.start()
            } catch (Exception e) {
                unauthorized()
            }
        }
    }

    void showPanel(JPanel jPanel) {
        SwingUtilities.invokeLater(new Runnable() {
            void run() {
                mainFrame.contentPane = jPanel
                jPanel.revalidate()
                mainFrame.repaint()
            }
        })
    }

    void unauthorized() {
        authorizationTimer.stop()
        showPanel(unauthorizedPanel)
    }

    void retryAuthorization() {
        showPanel(anonymousPanel)
        authorized()
    }

    @Override
    void run(ApplicationArguments args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        init()
        authorized()
    }

    void init() {
        adminPanel.add(new JLabel("Welcome to Admin Panel."))
        unauthorizedPanel.add(new JLabel("Sorry there is an authorization error."))
        unauthorizedPanel.add(swing.button(
                text: "Retry authorization",
                actionPerformed: {
                    retryAuthorization()
                }
        ))
        mainPanel.add(anonymousPanel)
        mainPanel.add(unauthorizedPanel)
        mainPanel.add(adminPanel)
    }

}