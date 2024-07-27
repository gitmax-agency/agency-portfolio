export class Constants {
    static readonly USER_TYPE_BUYER = 1;
    static readonly USER_TYPE_BUYERS_LENDER = 2;
    static readonly USER_TYPE_SELLER = 3;
    static readonly USER_TYPE_SELLERS_LENDER = 4;
    static readonly USER_TYPE_ARBITER = 5;
    static readonly USER_TYPE_REGISTRATOR = 6;
    static readonly USER_TYPE_BROKER = 7;

    static readonly STATE_NEW = 0;
    static readonly STATE_FUNDING_DEPOSIT = 1;
    static readonly STATE_CONTINGENCIES = 2;
    static readonly STATE_CONTINGENCIES_FUNDING = 3;
    static readonly STATE_CONTINGENCIES_DISPUTE = 4;
    static readonly STATE_FUNDING_PURCHASE = 5;
    static readonly STATE_REGISTRATION = 6;
    static readonly STATE_REGISTRATION_FUNDING = 7;
    static readonly STATE_REGISTRATION_DISPUTE = 8;
    static readonly STATE_COMPLETED = 9;
    static readonly STATE_FAILED = 10;

}