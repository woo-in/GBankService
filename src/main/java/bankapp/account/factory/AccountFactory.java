//// 이 클래스는 account 패키지 내의 service 또는 factory와 같은 하위 패키지에 위치시키면 좋습니다.
//package bankapp.account.factory;
//
//import bankapp.account.exceptions.UnsupportedAccountTypeException;
//import bankapp.account.model.account.*;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//import java.util.function.Supplier;
//
//
//@Component
//public class AccountFactory {
//
//    // 계좌 타입(문자열)과 계좌 생성 로직(Supplier)을 매핑하는 Map
//    private final Map<String, Supplier<Account>> accountCreators;
//
//    public AccountFactory() {
//        // 여기에 새로운 계좌 타입을 추가하기만 하면 됩니다.
//        this.accountCreators = Map.of(
//                "PRIMARY", PrimaryAccount::new ,
//                "SAVINGS", SavingsAccount::new
//        );
//    }
//    // PrimaryAccount::new : 이것이 바로 Supplier
//    // () -> new PrimaryAccount() 람다식과 완전히 동일한 의미
//    // 이 Supplier 객체의 get() 메소드를 호출하면 new PrimaryAccount() 가 실행
//
//
//
//    /**
//     * 계좌 타입 문자열을 기반으로 적절한 Account 객체를 생성합니다.
//     * @param accountType "PRIMARY", "DEPOSIT" 등의 계좌 타입
//     * @return 생성된 Account 객체 (PrimaryAccount 등)
//     */
//    public Account createAccount(String accountType) {
//        // Map에서 accountType에 해당하는 Supplier(생성자)를 찾습니다.
//        Supplier<Account> creator = accountCreators.get(accountType.toUpperCase());
//
//        if (creator == null) {
//            throw new UnsupportedAccountTypeException("지원하지 않는 계좌 타입입니다: " + accountType);
//        }
//
//        // Supplier.get()을 호출하여 새 객체를 생성하고 반환합니다. (예: new PrimaryAccount())
//        return creator.get();
//    }
//}