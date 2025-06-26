package bankapp.service.find;

import bankapp.account.request.creation.BankAccount;


public interface FindAccountService {

    BankAccount findAccount(int accountNumber) ;

}
