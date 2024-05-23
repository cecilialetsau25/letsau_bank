package com.webapp.letsau_bank.user;

import com.itextpdf.kernel.colors.ColorConstants;
import com.webapp.letsau_bank.account.AccountService;
import com.webapp.letsau_bank.account.Account;
import com.webapp.letsau_bank.account.Transaction;
import com.webapp.letsau_bank.account.TransactionService;
import com.webapp.letsau_bank.transaction.TransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
public class UserController {

    @Autowired private UserService userService;
    @Autowired private AccountService accountService;
    @Autowired private TransactionService transactionService;
    @Autowired private TransactionRepository transactionRepository;

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/add/user")
    public String goToAddUser() {
        return "add_user";
    }

    @GetMapping("/login/user")
    public String goToLogin() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String goToDashboard(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Account account = (Account) session.getAttribute("account");

        model.addAttribute("account", account);
        model.addAttribute("user", user);
        return "dashboard";
    }

    @PostMapping("/register/new/user")
    public String saveUser(@RequestParam("name") String name,
                           @RequestParam("surname") String surname,
                           @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("accountType") String accountType) {
        User user = new User(name, surname, username, password);

        Random rand = new Random();

        Long accountNumber = 90000000 + rand.nextLong(10000000);

        String sAccountNumber = String.valueOf(accountNumber);

        Account account = new Account();

        account.setBalance(0.0);
        account.setAccountType(accountType);
        account.setAccountNumber(sAccountNumber);
        account.setUser(user);

        userService.save(user);
        accountService.save(account);


        return "results";
    }

    @PostMapping("/validate/login")
    public String goToDashboard(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                Model model, HttpServletRequest request){

        HttpSession session = request.getSession(true);

        User user = userService.getByUsernameAndPassword(username, password);

        if(user == null){
            model.addAttribute("message", "Invalid username or password");
            return "login";
        }

        Account account = accountService.getAccount(user);
        model.addAttribute("account", account);
        model.addAttribute("user", user);
        session.setAttribute("user", user);
        session.setAttribute("account", account);
        return "dashboard";
    }

    @GetMapping("/deposit")
    public String goToDeposit(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Account account = (Account) session.getAttribute("account");

        model.addAttribute("account", account);
        model.addAttribute("user", user);
        return "deposit";
    }

    @GetMapping("/withdraw")
    public String goToWithdraw(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Account account = (Account) session.getAttribute("account");

        model.addAttribute("account", account);
        model.addAttribute("user", user);
        return "withdraw";
    }

    @PostMapping("/deposit/balance")
    public String updateBalanceAfterDeposit(@RequestParam("depositAmount") Double depositAmount,
                                            HttpServletRequest request, Model model,
                                            RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Account account = (Account) session.getAttribute("account");

        Double currentBalance = account.getBalance();
        account.setBalance(currentBalance + depositAmount);
        accountService.save(account);

        // Record transaction
        Transaction depositTransaction = new Transaction(account, "Deposit", depositAmount, "Deposit to account", new Date());
        transactionService.save(depositTransaction);

        model.addAttribute("account", account);
        model.addAttribute("user", user);
        session.setAttribute("user", user);
        session.setAttribute("account", account);

        redirectAttributes.addFlashAttribute("message", "Deposit successful");
        return "redirect:/dashboard";
    }

    @PostMapping("/withdraw/balance")
    public String updateBalanceAfterWithdrawal(@RequestParam("withdrawAmount") Double withdrawAmount,
                                               HttpServletRequest request, Model model,
                                               RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Account account = (Account) session.getAttribute("account");

        Double currentBalance = account.getBalance();
        if (currentBalance >= withdrawAmount) {
            account.setBalance(currentBalance - withdrawAmount);
            accountService.save(account);

            // Record transaction
            Transaction withdrawalTransaction = new Transaction(account, "Withdrawal", withdrawAmount, "Withdrawal from account", new Date());
            transactionService.save(withdrawalTransaction);

            model.addAttribute("account", account);
            model.addAttribute("user", user);
            session.setAttribute("user", user);
            session.setAttribute("account", account);

            redirectAttributes.addFlashAttribute("message", "Withdrawal successful");
        } else {
            redirectAttributes.addFlashAttribute("message", "Insufficient funds for the requested withdrawal");
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/bank/statement")
    public ResponseEntity<byte[]> generateBankStatement(HttpServletRequest request) throws IOException {

        // Get user and account details from session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Account account = (Account) session.getAttribute("account");

        // Get transactions for the account
        List<Transaction> transactions = transactionRepository.findByAccount(account);

        // Create a PDF document
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        Document doc = new Document(pdfDoc);

        // Add header
        doc.add(new Paragraph("Bank Statement")
                .setFontColor(ColorConstants.BLUE)
                .setBold()
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Add user and account details
        doc.add(new Paragraph("User: " + user.getName() + " " + user.getSurname()));
        doc.add(new Paragraph("Account Number: " + account.getAccountNumber()));
        doc.add(new Paragraph("Account Type: " + account.getAccountType()));
        doc.add(new Paragraph("Balance: " + account.getBalance()));

        // Add transactions table
        Table table = new Table(4);
        table.addCell("Transaction ID");
        table.addCell("Type");
        table.addCell("Amount");
        table.addCell("Description");

        for (Transaction transaction : transactions) {
            table.addCell(String.valueOf(transaction.getId()));
            table.addCell(transaction.getType());
            table.addCell(String.valueOf(transaction.getAmount()));
            table.addCell(transaction.getDescription());
        }

        doc.add(table);

        doc.close();

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "bank_statement.pdf");

        // Return the PDF as bytes
        return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
    }

}
