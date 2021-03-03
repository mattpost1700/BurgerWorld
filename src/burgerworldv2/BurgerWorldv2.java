package burgerworldv2;

import com.sun.management.jmx.Trace;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import static javafx.application.Application.launch;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;

/**
 *
 * @author mattp
 */
public class BurgerWorldv2 extends Application {

    public void start(Stage primaryStage) {
        ArrayList<Product> productDatabaseArray = new ArrayList<>();
        ArrayList<Account> accountDatabaseArray = new ArrayList<>();
        DatabaseConnector con = new DatabaseConnector();
        con.bootstrap(accountDatabaseArray, productDatabaseArray);

        File iconFile = new File("resources/icon.png");
        primaryStage.getIcons().add(new Image(iconFile.toURI().toString()));

        File burgerAndFriesFile = new File("resources/burgerAndFries.jpg");
        Image burgerAndFries = new Image(burgerAndFriesFile.toURI().toString());
        Button orderFoodButton = new Button("", new ImageView(burgerAndFries));
        orderFoodButton.setStyle("-fx-background-color: #000000");
        orderFoodButton.setMinWidth(200);
        orderFoodButton.setMinHeight(160);

        File stockRoomFile = new File("resources/stockRoom2.png");
        Image stockRoom = new Image(stockRoomFile.toURI().toString());
        Button orderSupplyButton = new Button("", new ImageView(stockRoom));
        orderSupplyButton.setStyle("-fx-background-color: #000000");
        orderSupplyButton.setMinWidth(200);
        orderSupplyButton.setMinHeight(160);

        orderFoodButton.setOnAction((ActionEvent event) -> {
            Stage accountLogin = new Stage();
            accountLogin.setMinHeight(135);
            accountLogin.setMinWidth(265);
            FlowPane root = new FlowPane();
            root.setAlignment(Pos.CENTER);
            accountLogin.getIcons().add(new Image(iconFile.toURI().toString()));
            VBox vb = new VBox(5);

            TextField accountTF = new TextField("account ID");
            TextField passwordTF = new TextField("password");
            Button submitButton = new Button("Login");

            vb.setAlignment(Pos.CENTER);
            vb.getChildren().addAll(accountTF, passwordTF, submitButton);
            root.getChildren().add(vb);

            submitButton.setOnAction((ActionEvent e) -> {
                String accountID = accountTF.getText();
                String password = passwordTF.getText();
                for (Account a : accountDatabaseArray) {
                    if (a.isValidAccount(accountID, password)) {
                        Receipt receipt = new Receipt();
                        createOrderMenu(primaryStage, productDatabaseArray, a, receipt);
                    }
                }
                accountLogin.close();
            });

            root.setPadding(new Insets(5, 5, 5, 5));

            Scene scene = new Scene(root, 255, 105);

            accountLogin.setTitle("Account Login");
            accountLogin.setScene(scene);
            accountLogin.show();
        });

        orderSupplyButton.setOnAction((ActionEvent event) -> {
            Stage managerLogin = new Stage();
            managerLogin.setMinHeight(105);
            managerLogin.setMinWidth(285);
            FlowPane root = new FlowPane();
            root.setAlignment(Pos.CENTER);
            managerLogin.getIcons().add(new Image(iconFile.toURI().toString()));

            TextField pinTF = new TextField("Manager PIN");
            Button submitButton = new Button("Login");
            VBox vb = new VBox(5);
            vb.getChildren().addAll(pinTF, submitButton);
            vb.setAlignment(Pos.CENTER);

            root.getChildren().add(vb);

            submitButton.setOnAction((ActionEvent e) -> {
                Integer pin = new Integer(-1);
                try {
                    pin = Integer.parseInt(pinTF.getText());
                } catch (Exception ex) {
                }
                if (validate(pin)) {
                    //System.out.println("Success!");
                    createSupplyScreen(primaryStage, productDatabaseArray, con);
                } else {
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                        Date date = new Date(System.currentTimeMillis());
                        formatter.format(date);
                        String s = date + "\n" + "Failed attempt was made to login to inventory login with pin: " + pinTF.getText() + "\n\n";
                        Files.write(Paths.get("log/FAILED_MANAGER_LOGIN_ATTEMPTS.txt"), s.getBytes(), StandardOpenOption.APPEND);
                        //System.out.println("Failed Attempt!");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                managerLogin.close();
            });

            Scene scene = new Scene(root, 200, 70);

            managerLogin.setTitle("Manager Login");
            managerLogin.setScene(scene);
            managerLogin.show();
        });

        HBox root = new HBox(50);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(40, 40, 40), CornerRadii.EMPTY, Insets.EMPTY)));
        root.getChildren().add(orderFoodButton);
        root.getChildren().add(orderSupplyButton);

        //root.setPadding(new Insets(5, 5, 5, 5));
        root.setPadding(new Insets(20, 20, 20, 20));
        root.setAlignment(Pos.BASELINE_CENTER);

        //Scene scene = new Scene(root, 430, 160);
        Scene scene = new Scene(root, 500, 190);

        primaryStage.setTitle("Burger World");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void createSupplyScreen(Stage stage, ArrayList<Product> productDatabaseArray, DatabaseConnector con) {
        con.refreshDatabase(productDatabaseArray);
        Button visRepButton = new Button("Visual Representation");
        Button refreshButton = new Button("Refresh Inventory");

        visRepButton.setOnAction((ActionEvent event) -> {
            createVisRep(productDatabaseArray, con);
        });

        TableView table = new TableView();
        Label label = new Label("Inventory");
        TableColumn itemCol = new TableColumn("Item");
        itemCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn amountCol = new TableColumn("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amountOfProduct"));
        TableColumn priceCol = new TableColumn("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn relCol = new TableColumn("Percent Filled");
        relCol.setCellValueFactory(new PropertyValueFactory<>("relativity"));
        TableColumn reorderCol = new TableColumn("Reorder");
        reorderCol.setSortable(false);
        reorderCol.setCellValueFactory(new PropertyValueFactory<>("button"));

        table.getColumns().addAll(itemCol, amountCol, priceCol, relCol, reorderCol);
        for (Product p : productDatabaseArray) {
            table.getItems().add(p);
        }

        refreshButton.setOnAction((ActionEvent event) -> {
            con.refreshDatabase(productDatabaseArray);
            table.getItems().clear();
            for (Product p : productDatabaseArray) {
                table.getItems().add(p);
            }
        });

        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5, 10, 0, 10));
        vBox.getChildren().addAll(label, table);

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(7, 0, 0, 0));
        borderPane.setCenter(visRepButton);
        borderPane.setBottom(refreshButton);

        vBox.getChildren().add(borderPane);
        Scene scene = new Scene(vBox, 500, 500);

        stage.setScene(scene);
        stage.show();
    }

    public void createVisRep(ArrayList<Product> productDatabaseArray, DatabaseConnector con) {
        con.refreshDatabase(productDatabaseArray);
        Stage graphWindow = new Stage();
        File iconFile = new File("resources/icon.png");
        graphWindow.getIcons().add(new Image(iconFile.toURI().toString()));
        graphWindow.setTitle("Burger World");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis("Percentage", 0, 100, 10);
        StackedBarChart<String, Number> barChart = new StackedBarChart<>(xAxis, yAxis);
        barChart.setTitle("Inventory Summary");
        xAxis.setLabel("Product");

        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        series1.setName("Percentage Filled");
        series2.setName("Percentage Used");
        for (Product p : productDatabaseArray) {
            series1.getData().add(new XYChart.Data(p.getName(), p.getRelativity()));
            series2.getData().add(new XYChart.Data(p.getName(), 100 - p.getRelativity()));
        }

        barChart.getData().addAll(series1, series2);

        Scene scene = new Scene(barChart, 800, 600);
        barChart.getScene().getStylesheets().add(getClass().getResource("barChart.css").toExternalForm());

        graphWindow.setScene(scene);
        graphWindow.show();
    }

    public void createOrderMenu(Stage stage, ArrayList<Product> productDatabaseArray, Account account, Receipt receipt) {
        double btnMinWidth = 150;
        double btnMinHeight = 150;
        double btnMaxWidth = 150;
        double btnMaxHeight = 150;
        String formatting = "; -fx-font-size: 25px; -fx-font-weight:bold; -fx-text-fill: white";

        Button burgerButton = new Button("Burger");
        burgerButton.setStyle("-fx-background-color: #0000ff" + formatting);
        burgerButton.setMinWidth(btnMinWidth);
        burgerButton.setMinHeight(btnMinHeight);
        burgerButton.setMaxWidth(btnMaxWidth);
        burgerButton.setMaxHeight(btnMaxHeight);
        burgerButton.setOnAction((ActionEvent e) -> {
            createBurgerScreen(stage, receipt, account, productDatabaseArray);
        });

        Button hotdogButton = new Button("Hot\nDog");
        hotdogButton.setStyle("-fx-background-color: #00ff80" + formatting);
        hotdogButton.setMinWidth(btnMinWidth);
        hotdogButton.setMinHeight(btnMinHeight);
        hotdogButton.setMaxWidth(btnMaxWidth);
        hotdogButton.setMaxHeight(btnMaxHeight);
        hotdogButton.setOnAction((ActionEvent e) -> {
            createHotdogScreen(stage, receipt, account, productDatabaseArray);
        });

        Button saladButton = new Button("Salad");
        saladButton.setStyle("-fx-background-color: #80ff00" + formatting);
        saladButton.setMinWidth(btnMinWidth);
        saladButton.setMinHeight(btnMinHeight);
        saladButton.setMaxWidth(btnMaxWidth);
        saladButton.setMaxHeight(btnMaxHeight);
        saladButton.setOnAction((ActionEvent e) -> {
            createSaladScreen(stage, receipt, account, productDatabaseArray);
        });

        Button tenderButton = new Button("Chicken\nTenders");
        tenderButton.setStyle("-fx-background-color: #f50020" + formatting);
        tenderButton.setMinWidth(btnMinWidth);
        tenderButton.setMinHeight(btnMinHeight);
        tenderButton.setMaxWidth(btnMaxWidth);
        tenderButton.setMaxHeight(btnMaxHeight);
        tenderButton.setOnAction((ActionEvent e) -> {
            createTenderScreen(stage, receipt, account, productDatabaseArray);
        });

        Button drinkButton = new Button("Drinks");
        drinkButton.setStyle("-fx-background-color: #008080" + formatting);
        drinkButton.setMinWidth(btnMinWidth);
        drinkButton.setMinHeight(btnMinHeight);
        drinkButton.setMaxWidth(btnMaxWidth);
        drinkButton.setMaxHeight(btnMaxHeight);
        drinkButton.setOnAction((ActionEvent e) -> {
            createDrinkScreen(stage, receipt, account, productDatabaseArray);
        });

        Button checkoutButton = new Button("Checkout");
        checkoutButton.setStyle("-fx-background-color: #00ffff" + formatting);
        checkoutButton.setMinWidth(btnMinWidth);
        checkoutButton.setMinHeight(btnMinHeight);
        checkoutButton.setMaxWidth(btnMaxWidth);
        checkoutButton.setMaxHeight(btnMaxHeight);
        checkoutButton.setOnAction((ActionEvent e) -> {
            createCheckout(stage, receipt, account, productDatabaseArray);
        });

        GridPane root = new GridPane();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(40, 40, 40), CornerRadii.EMPTY, Insets.EMPTY)));
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10));

        root.add(burgerButton, 0, 0);
        GridPane.setHalignment(burgerButton, HPos.LEFT);
        root.add(hotdogButton, 1, 0);
        root.add(checkoutButton, 2, 0);
        root.add(saladButton, 0, 1);
        root.add(tenderButton, 1, 1);
        root.add(drinkButton, 2, 1);

        Scene scene = new Scene(root, 490, 330);

        stage.setTitle("Order Menu");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void createBurgerScreen(Stage stage, Receipt receipt, Account account, ArrayList<Product> productDatabaseArray) {
        ArrayList<FoodFeature> features = new ArrayList<>();
        Food burger = new Food("Burger", 5, features);

        Label bunLbl = new Label("Bun: ");
        ToggleGroup bunGroup = new ToggleGroup();
        RadioButton potatoBunRB = new RadioButton("Potato");
        potatoBunRB.setToggleGroup(bunGroup);
        RadioButton pretzelBunRB = new RadioButton("Pretzel +$0.50");
        pretzelBunRB.setToggleGroup(bunGroup);

        Label toppingLbl = new Label("Toppings: ");
        CheckBox lettuceCB = new CheckBox("Lettuce");
        CheckBox onionCB = new CheckBox("Red Onion");
        CheckBox tomatoCB = new CheckBox("Tomato");
        CheckBox ketchupCB = new CheckBox("Ketchup");

        Label pattyLbl = new Label("Patty: ");
        ToggleGroup pattyGroup = new ToggleGroup();
        RadioButton beefRB = new RadioButton("Beef");
        beefRB.setToggleGroup(pattyGroup);
        RadioButton chickenRB = new RadioButton("Chicken");
        chickenRB.setToggleGroup(pattyGroup);
        RadioButton vegRB = new RadioButton("Veggie");
        vegRB.setToggleGroup(pattyGroup);

        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setOnAction((ActionEvent e) -> {
            if (potatoBunRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Bread", 100));
                burger.getFeatureArr().add(new FoodFeature("Potato Bun", temp, 0));
            }
            if (pretzelBunRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Bread", 200));
                burger.getFeatureArr().add(new FoodFeature("Pretzel Bun", temp, 0.5));
            }
            if (beefRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Beef", 100));
                burger.getFeatureArr().add(new FoodFeature("Beef", temp, 0));
            }
            if (chickenRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Chicken", 100));
                burger.getFeatureArr().add(new FoodFeature("Chicken", temp, 0));
            }
            if (vegRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Vegtable", 100));
                burger.getFeatureArr().add(new FoodFeature("Veggie", temp, 0));
            }
            if (lettuceCB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Vegtable", 20));
                burger.getFeatureArr().add(new FoodFeature("Lettuce", temp, 0));
            }
            if (onionCB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Vegtable", 15));
                burger.getFeatureArr().add(new FoodFeature("Red Onion", temp, 0));
            }
            if (tomatoCB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Vegtable", 20));
                burger.getFeatureArr().add(new FoodFeature("Tomato", temp, 0));
            }
            if (ketchupCB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Vegtable", 10));
                temp.add(new Product("Spice", 10));
                burger.getFeatureArr().add(new FoodFeature("Ketchup", temp, 0));
            }
            receipt.addFood(burger);
            createCheckout(stage, receipt, account, productDatabaseArray);
        });

        Label costLbl = new Label("Burger Base Cost: $ " + burger.getPrice());

        GridPane root = new GridPane();
        root.add(bunLbl, 0, 0);
        root.add(potatoBunRB, 0, 1);
        root.add(pretzelBunRB, 0, 2);
        root.add(toppingLbl, 0, 4);
        root.add(lettuceCB, 0, 5);
        root.add(onionCB, 0, 6);
        root.add(tomatoCB, 0, 7);
        root.add(ketchupCB, 0, 8);
        root.add(pattyLbl, 0, 10);
        root.add(beefRB, 0, 11);
        root.add(chickenRB, 0, 12);
        root.add(vegRB, 0, 13);

        root.add(checkoutBtn, 1, 0);
        root.add(costLbl, 0, 14);
        root.setPadding(new Insets(5, 5, 5, 5));

        Scene scene = new Scene(root, 250, 250);
        stage.setScene(scene);
        stage.show();
    }

    public void createHotdogScreen(Stage stage, Receipt receipt, Account account, ArrayList<Product> productDatabaseArray) {
        ArrayList<FoodFeature> features = new ArrayList<>();
        Food hotdog = new Food("Hotdog", 1.5, features);

        Label bunLbl = new Label("Bun: ");
        ToggleGroup bunGroup = new ToggleGroup();
        RadioButton potatoBunRB = new RadioButton("Potato");
        potatoBunRB.setToggleGroup(bunGroup);
        RadioButton pretzelBunRB = new RadioButton("Pretzel +$0.50");
        pretzelBunRB.setToggleGroup(bunGroup);

        Label toppingLbl = new Label("Toppings: ");
        CheckBox relishCB = new CheckBox("Relish");
        CheckBox onionCB = new CheckBox("Onion");
        CheckBox mustardCB = new CheckBox("Mustard");
        CheckBox ketchupCB = new CheckBox("Ketchup");

        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setOnAction((ActionEvent e) -> {
            createCheckout(stage, receipt, account, productDatabaseArray);
            if (potatoBunRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Bread", 100));
                temp.add(new Product("Beef", 100));
                hotdog.getFeatureArr().add(new FoodFeature("Potato Bun", temp, 0));
            }
            if (pretzelBunRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Bread", 150));
                temp.add(new Product("Beef", 100));
                hotdog.getFeatureArr().add(new FoodFeature("Pretzel Bun", temp, 0.5));
            }
            if (relishCB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Vegtable", 25));
                temp.add(new Product("Spice", 5));
                hotdog.getFeatureArr().add(new FoodFeature("Relish", temp, 0));
            }
            if (onionCB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Vegtable", 25));
                hotdog.getFeatureArr().add(new FoodFeature("Onion", temp, 0));
            }
            if (mustardCB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Spice", 20));
                temp.add(new Product("Water", 5));
                temp.add(new Product("Vegtable", 5));
                hotdog.getFeatureArr().add(new FoodFeature("Mustard", temp, 0));
            }
            if (ketchupCB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Spice", 15));
                temp.add(new Product("Water", 5));
                temp.add(new Product("Vegtable", 15));
                hotdog.getFeatureArr().add(new FoodFeature("Ketchup", temp, 0));
            }
            receipt.addFood(hotdog);
            createCheckout(stage, receipt, account, productDatabaseArray);
        });

        Label costLbl = new Label("Hot Dog Base Cost: $ " + hotdog.getPrice());

        GridPane root = new GridPane();
        root.add(bunLbl, 0, 0);
        root.add(potatoBunRB, 0, 1);
        root.add(pretzelBunRB, 0, 2);
        root.add(toppingLbl, 0, 3);
        root.add(relishCB, 0, 4);
        root.add(onionCB, 0, 5);
        root.add(mustardCB, 0, 6);
        root.add(ketchupCB, 0, 7);

        root.add(checkoutBtn, 1, 0);
        root.add(costLbl, 0, 8);
        root.setPadding(new Insets(5, 5, 5, 5));

        Scene scene = new Scene(root, 250, 200);
        stage.setScene(scene);
        stage.show();
    }

    public void createSaladScreen(Stage stage, Receipt receipt, Account account, ArrayList<Product> productDatabaseArray) {
        ArrayList<FoodFeature> features = new ArrayList<>();
        Food salad = new Food("Salad", 3, features);

        Label sizeLbl = new Label("Size: ");
        ToggleGroup sizeGroup = new ToggleGroup();
        RadioButton smallRB = new RadioButton("Small");
        smallRB.setToggleGroup(sizeGroup);
        RadioButton largeRB = new RadioButton("Large +$2.00");
        largeRB.setToggleGroup(sizeGroup);

        Label toppingLbl = new Label("Additional Add-ons: ");
        CheckBox chickenCB = new CheckBox("Chicken +$4.00");
        CheckBox tunaCB = new CheckBox("Tuna +$3.00");
        CheckBox croutonCB = new CheckBox("Croutons +$0.50");

        Label dressingLbl = new Label("Dressing: ");
        ToggleGroup pattyGroup = new ToggleGroup();
        RadioButton italianRB = new RadioButton("Italian");
        italianRB.setToggleGroup(pattyGroup);
        RadioButton balsamicRB = new RadioButton("Balsamic +$0.50");
        balsamicRB.setToggleGroup(pattyGroup);
        RadioButton ranchRB = new RadioButton("Ranch +$0.50");
        ranchRB.setToggleGroup(pattyGroup);

        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setOnAction((ActionEvent e) -> {
            if (smallRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                salad.getFeatureArr().add(new FoodFeature("Small", 0));
            }
            if (largeRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                salad.getFeatureArr().add(new FoodFeature("Large", 2));
            }
            if (chickenCB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Chicken", 75));
                salad.getFeatureArr().add(new FoodFeature("Chicken", 4));
            }
            if (tunaCB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                //I know tuna is not chicken but idk what else to take from lol
                temp.add(new Product("Chicken", 60));
                temp.add(new Product("Spice", 10));
                salad.getFeatureArr().add(new FoodFeature("Tuna", 3));
            }
            if (croutonCB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Bread", 50));
                temp.add(new Product("Spice", 10));
                salad.getFeatureArr().add(new FoodFeature("Croutons", .5));
            }
            if (italianRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Water", 90));
                temp.add(new Product("Spice", 15));
                temp.add(new Product("Vegtable", 5));
                salad.getFeatureArr().add(new FoodFeature("Italian Dressing", 0));
            }
            if (balsamicRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Water", 60));
                temp.add(new Product("Spice", 35));
                temp.add(new Product("Vegtable", 5));
                salad.getFeatureArr().add(new FoodFeature("Balsamic Dressing", .5));
            }
            if (ranchRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Spice", 45));
                temp.add(new Product("Vegtable", 15));
                temp.add(new Product("Fruit", 15));
                salad.getFeatureArr().add(new FoodFeature("Ranch Dressing", .5));
            }
            receipt.addFood(salad);
            createCheckout(stage, receipt, account, productDatabaseArray);
        });

        Label costLbl = new Label("Salad Base Cost: $ " + salad.getPrice());

        GridPane root = new GridPane();
        root.add(sizeLbl, 0, 0);
        root.add(smallRB, 0, 1);
        root.add(largeRB, 0, 2);
        root.add(toppingLbl, 0, 4);
        root.add(chickenCB, 0, 5);
        root.add(tunaCB, 0, 6);
        root.add(croutonCB, 0, 7);
        root.add(dressingLbl, 0, 10);
        root.add(italianRB, 0, 11);
        root.add(balsamicRB, 0, 12);
        root.add(ranchRB, 0, 13);

        root.add(checkoutBtn, 1, 0);
        root.add(costLbl, 0, 14);
        root.setPadding(new Insets(5, 5, 5, 5));

        Scene scene = new Scene(root, 250, 240);
        stage.setScene(scene);
        stage.show();
    }

    public void createTenderScreen(Stage stage, Receipt receipt, Account account, ArrayList<Product> productDatabaseArray) {
        ArrayList<FoodFeature> features = new ArrayList<>();
        Food tenders = new Food("Chicken Tenders", 5, features);

        Label sizeLbl = new Label("Size: ");
        ToggleGroup sizeGroup = new ToggleGroup();
        RadioButton smallRB = new RadioButton("Small");
        smallRB.setToggleGroup(sizeGroup);
        RadioButton largeRB = new RadioButton("Large +$2.00");
        largeRB.setToggleGroup(sizeGroup);

        Label dipsLbl = new Label("Dips: ");
        ToggleGroup dipGroup = new ToggleGroup();
        RadioButton ketchupRB = new RadioButton("Ketchup");
        ketchupRB.setToggleGroup(dipGroup);
        RadioButton mustardRB = new RadioButton("Mustard +$0.50");
        mustardRB.setToggleGroup(dipGroup);
        RadioButton ranchRB = new RadioButton("Ranch +$0.50");
        ranchRB.setToggleGroup(dipGroup);

        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setOnAction((ActionEvent e) -> {
            if (smallRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                tenders.getFeatureArr().add(new FoodFeature("Small", 0));
            }
            if (largeRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                tenders.getFeatureArr().add(new FoodFeature("Large", 2));
            }
            if (ketchupRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Spice", 45));
                temp.add(new Product("Vegtable", 15));
                tenders.getFeatureArr().add(new FoodFeature("Ketchup", 0));
            }
            if (mustardRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Spice", 65));
                temp.add(new Product("Vegtable", 15));
                temp.add(new Product("Fruit", 5));
                tenders.getFeatureArr().add(new FoodFeature("Honey Mustard", .5));
            }
            if (ranchRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Spice", 45));
                temp.add(new Product("Vegtable", 20));
                temp.add(new Product("Fruit", 15));
                tenders.getFeatureArr().add(new FoodFeature("Ranch", .5));
            }
            receipt.addFood(tenders);
            createCheckout(stage, receipt, account, productDatabaseArray);
        });

        Label costLbl = new Label("Chicken Tender Base Cost: $ " + tenders.getPrice());

        GridPane root = new GridPane();
        root.add(sizeLbl, 0, 0);
        root.add(smallRB, 0, 1);
        root.add(largeRB, 0, 2);
        root.add(dipsLbl, 0, 3);
        root.add(ketchupRB, 0, 4);
        root.add(mustardRB, 0, 5);
        root.add(ranchRB, 0, 6);

        root.add(checkoutBtn, 1, 0);
        root.add(costLbl, 0, 7);
        root.setPadding(new Insets(5, 5, 5, 5));

        Scene scene = new Scene(root, 280, 150);
        stage.setScene(scene);
        stage.show();
    }

    public void createDrinkScreen(Stage stage, Receipt receipt, Account account, ArrayList<Product> productDatabaseArray) {
        ArrayList<FoodFeature> features = new ArrayList<>();
        Food drink = new Food("Drink", 0, features);

        Label sizeLbl = new Label("Size: ");
        ToggleGroup sizeGroup = new ToggleGroup();
        RadioButton smallRB = new RadioButton("Small");
        smallRB.setToggleGroup(sizeGroup);
        RadioButton largeRB = new RadioButton("Large +$1.00");
        largeRB.setToggleGroup(sizeGroup);

        Label drinkLbl = new Label("Drink: ");
        ToggleGroup drinkGroup = new ToggleGroup();
        RadioButton waterRB = new RadioButton("Water");
        waterRB.setToggleGroup(drinkGroup);
        RadioButton ojRB = new RadioButton("Orange Juice +$2.00");
        ojRB.setToggleGroup(drinkGroup);
        RadioButton fountainDrinkRB = new RadioButton("Fountain Drink +$1.00");
        fountainDrinkRB.setToggleGroup(drinkGroup);

        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setOnAction((ActionEvent e) -> {
            if (smallRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                drink.getFeatureArr().add(new FoodFeature("Small", 0));
            }
            if (largeRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                drink.getFeatureArr().add(new FoodFeature("Large", 1));
            }
            if (waterRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Water", 100));
                drink.getFeatureArr().add(new FoodFeature("Water", 0));
            }
            if (ojRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Water", 80));
                temp.add(new Product("Fruit", 50));
                drink.getFeatureArr().add(new FoodFeature("Orange Juice", 2));
            }
            if (fountainDrinkRB.isSelected()) {
                ArrayList<Product> temp = new ArrayList<>();
                temp.add(new Product("Water", 80));
                temp.add(new Product("Spice", 5));
                drink.getFeatureArr().add(new FoodFeature("Fountain Drink", 1));
            }
            receipt.addFood(drink);
            createCheckout(stage, receipt, account, productDatabaseArray);
        });

        Label costLbl = new Label("Drink Base Cost: $ " + drink.getPrice());

        GridPane root = new GridPane();
        root.add(sizeLbl, 0, 0);
        root.add(smallRB, 0, 1);
        root.add(largeRB, 0, 2);
        root.add(drinkLbl, 0, 3);
        root.add(waterRB, 0, 4);
        root.add(ojRB, 0, 5);
        root.add(fountainDrinkRB, 0, 6);

        root.add(checkoutBtn, 1, 0);
        root.add(costLbl, 0, 7);
        root.setPadding(new Insets(5, 5, 5, 5));

        Scene scene = new Scene(root, 230, 200);
        stage.setScene(scene);
        stage.show();
    }

    public void createCheckout(Stage stage, Receipt receipt, Account account, ArrayList<Product> productDatabaseArray) {
        stage.setTitle("Checkout");
        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setMinHeight(50);
        checkoutBtn.setMinWidth(200);
        checkoutBtn.setStyle("-fx-background-color: #00ffff");
        checkoutBtn.setOnAction((ActionEvent event) -> {
            if (account.canPurchaseBeMade(receipt.getTotalPrice())) {
                String outOf = "";
                if (receipt.isThereEnoughInInventory(productDatabaseArray, outOf)) {
                    account.makePurchase(receipt.getTotalPrice());
                    receipt.takeOutOfInventory(productDatabaseArray);
                    createOrderAnimation(stage, receipt, account);
                } else {
                    checkoutBtn.setText("Out of: " + outOf);
                }
            } else {
                checkoutBtn.setText("Insuffienct Funds");
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setMinHeight(50);
        cancelBtn.setMinWidth(80);
        cancelBtn.setStyle("-fx-background-color: #ff0000");
        cancelBtn.setOnAction((ActionEvent event) -> {
            start(stage);
        });

        Button addFoodBtn = new Button("Add Food");
        addFoodBtn.setMinHeight(50);
        addFoodBtn.setMinWidth(110);
        addFoodBtn.setStyle("-fx-background-color: #00ff00");
        addFoodBtn.setOnAction((ActionEvent event) -> {
            createOrderMenu(stage, productDatabaseArray, account, receipt);
        });

        Label receiptLbl = receipt.getReceiptLabel();
        Label totalCostLbl = new Label("Total Cost: $" + receipt.getTotalPrice());

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(5, 5, 5, 5));
        GridPane buttonCluster = new GridPane();
        VBox itemsPane = new VBox();

        buttonCluster.add(checkoutBtn, 0, 0, 3, 1);
        buttonCluster.add(addFoodBtn, 0, 1, 1, 1);
        buttonCluster.add(cancelBtn, 2, 1, 1, 1);
        buttonCluster.setVgap(10);
        buttonCluster.setHgap(5);
        buttonCluster.setPadding(new Insets(30, 30, 0, 0));

        itemsPane.getChildren().add(receiptLbl);

        borderPane.setLeft(itemsPane);
        borderPane.setBottom(totalCostLbl);
        borderPane.setRight(buttonCluster);

        Scene scene = new Scene(borderPane, 450, 500);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void createOrderAnimation(Stage stage, Receipt receipt, Account account) {
        Task copyWorker;

        FlowPane root = new FlowPane();
        root.setPadding(new Insets(5, 5, 5, 5));

        final Label label = new Label("Order Completion:   ");
        final ProgressBar progressBar = new ProgressBar(0);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(label, progressBar);

        copyWorker = createWorker(receipt.getNumOfFood());
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(copyWorker.progressProperty());
        copyWorker.messageProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {

            Button completedButton = new Button("Close Window");
            FlowPane temp = new FlowPane();
            temp.setAlignment(Pos.CENTER);
            VBox vb = new VBox(10);
            vb.getChildren().addAll(new Label("    Completed!"), completedButton);
            temp.getChildren().addAll(vb);
            stage.setScene(new Scene(temp, 330, 120));
            completedButton.setOnAction((ActionEvent event) -> {
                stage.close();
            });
        });
        Scene scene = new Scene(root, 330, 120);
        new Thread(copyWorker).start();
        stage.setScene(scene);
        stage.show();
    }

    public Task createWorker(int time) {
        return new Task() {
            public Object call() throws Exception {
                for (int i = 0; i < 10 * (time * .5); i++) {
                    Thread.sleep(2000);
                    updateMessage("2000 milliseconds");
                    updateProgress(i + 1, 10);
                }
                return true;
            }
        };
    }

    public boolean validate(Integer pin) {
        return pin == 1234;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
