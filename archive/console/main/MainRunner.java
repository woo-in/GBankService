package bankapp.main;

import bankapp.UI.ConsoleUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MainRunner implements CommandLineRunner {

    private final ConsoleUI consoleUI;

    @Autowired
    public MainRunner(ConsoleUI consoleUI) {
        this.consoleUI = consoleUI;
    }

    @Override
    public void run(String... args) {
        while (true) {
            int command = consoleUI.showMenu();

            switch (command) {
                case 1:
                    consoleUI.readAccountTypeInfo();
                    break;
                case 2:
                    consoleUI.readDepositInfo();
                    break;
                case 3:
                    consoleUI.readWithdrawInfo();
                    break;
                case 4:
                    consoleUI.readTransferInfo();
                    break;
                case 5:
                    consoleUI.showAccountInfo();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("(1~6) 숫자를 입력해주세요!");
                    break;
            }
        }
    }
}