package bankapp.account.service.open.primary;

import bankapp.account.model.PrimaryAccount;
import bankapp.account.request.open.OpenPrimaryAccountRequest;

public interface OpenPrimaryAccountService {
    PrimaryAccount openPrimaryAccount(OpenPrimaryAccountRequest openPrimaryAccountRequest);
}
