/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burgerworldv2;

/**
 *
 * @author mattp
 */
class Account {
    
    int accountNum;
    String password;
    double accountBalance;
    String customerName;
    String customerAddress;
    String customerPhoneNum;

    public Account() {
    }

    public Account(int accountNum, String password, double accountBalance, String customerName, String customerAddress, String customerPhoneNum) {
        this.accountNum = accountNum;
        this.password = password;
        this.accountBalance = accountBalance;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPhoneNum = customerPhoneNum;
    }
    
    public int getAccountNum() {
        return accountNum;
    }

    public String getPassword() {
        return password;
    }

    boolean isValidAccount(String accountNum, String password) {
        String temp = this.accountNum + "";
        return (temp.equals(accountNum) && this.password.equals(password));
    }

    public boolean canPurchaseBeMade(double cost) {
        return accountBalance >= cost;
    }

    public void makePurchase(double totalPrice) {
        this.accountBalance -= totalPrice;
        //System.out.println(this.accountBalance + "    num" + accountNum);
        DatabaseConnector.updateAccount(accountBalance, accountNum);
    }

    double getAccountBalance() {
        return accountBalance;
    }

    String getCustomerName() {
        return customerName;
    }

    String getCustomerAddress() {
        return customerAddress;
    }

    String getCustomerPhoneNum() {
        return customerPhoneNum;
    }

    //for debugging purposes
    public String toString() {
        return "accountNum: " + accountNum + " Password: " + password + " AccountBalance: " + accountBalance + " AccountName " + customerName;
    }

}
