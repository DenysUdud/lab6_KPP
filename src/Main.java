import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Product implements Serializable {
    private String name;
    private String unit;
    private int quantity;
    private int price;
    private Date arrivalDate;
    private String description;

    public Product(String name, String unit, int quantity, int price, Date arrivalDate, String description) {
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.price = price;
        this.arrivalDate = arrivalDate;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", arrivalDate=" + arrivalDate +
                ", description='" + description + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }
}

class Store implements Serializable {
    private List<Product> products;

    public Store() {
        this.products = new ArrayList<>();
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getProducts() {
        return products;
    }

    @Override
    public String toString() {
        return "Store{" +
                "products=" + products +
                '}';
    }
}

class StoreContainer implements Serializable {
    private List<Store> stores;

    public StoreContainer() {
        this.stores = new ArrayList<>();
    }

    public void addStore(Store store) {
        stores.add(store);
    }

    public void removeStore(int index) {
        if (index >= 0 && index < stores.size()) {
            stores.remove(index);
        }
    }

    public List<Store> getStores() {
        return stores;
    }

    @Override
    public String toString() {
        return "StoreContainer{" +
                "stores=" + stores +
                '}';
    }
}

public class Main {
    private static final String STORES_FILE = "stores.dat";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String mode = scanner.nextLine();
        StoreContainer storeContainer = loadStoreContainer();

        // boolean autoMode = args.length > 0 && args[0].equals("-auto");

        if (mode.equals("-auto")) {
            // Generate or load data in automatic mode
            generateTestData(storeContainer);
        } else {
            // Interactive mode
            interactWithUser(scanner, storeContainer);
        }

        // Save data
        saveStoreContainer(storeContainer);
    }

    private static void interactWithUser(Scanner scanner, StoreContainer storeContainer) {
        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Add a new store");
            System.out.println("2. View list of stores");
            System.out.println("3. Remove a store");
            System.out.println("4. Exit program");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear the buffer

            switch (choice) {
                case 1:
                    System.out.print("Enter store name: ");
                    String storeName = scanner.nextLine();

                    Store newStore = new Store();
                    while (true) {
                        System.out.println("Add a product to the store (Y/N)?");
                        String addProductChoice = scanner.nextLine();
                        if (!addProductChoice.equalsIgnoreCase("Y")) {
                            break;
                        }

                        System.out.print("Product name: ");
                        String productName = scanner.nextLine();
                        System.out.print("Unit: ");
                        String unit = scanner.nextLine();
                        System.out.print("Quantity: ");
                        int quantity = scanner.nextInt();
                        System.out.print("Price: ");
                        int price = scanner.nextInt();
                        System.out.print("Arrival date (YYYY-MM-DD): ");
                        String dateStr = scanner.next();
                        Date arrivalDate = parseDate(dateStr);
                        scanner.nextLine(); // Clear the buffer
                        System.out.print("Description: ");
                        String description = scanner.nextLine();

                        Product newProduct = new Product(productName, unit, quantity, price, arrivalDate, description);
                        newStore.addProduct(newProduct);
                    }

                    storeContainer.addStore(newStore);
                    System.out.println("Store added!");
                    break;

                case 2:
                    System.out.println("List of stores:");
                    int index = 0;
                    for (Store store : storeContainer.getStores()) {
                        System.out.println(index + ". " + store);
                        index++;
                    }
                    break;

                case 3:
                    System.out.print("Enter the index of the store to remove: ");
                    int storeIndexToRemove = scanner.nextInt();
                    scanner.nextLine(); // Clear the buffer
                    storeContainer.removeStore(storeIndexToRemove);
                    System.out.println("Store removed (if found).");
                    break;

                case 4:
                    System.out.println("Program terminated.");
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }
    }

    private static void generateTestData(StoreContainer storeContainer) {
        // Generate or load test data in automatic mode
        // For demonstration purposes, generating some random data
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            Store store = new Store();
            storeContainer.addStore(store);

            for (int j = 0; j < 5; j++) {
                String productName = "Product" + (j + 1);
                String unit = "Unit";
                int quantity = random.nextInt(10) + 1;
                int price = random.nextInt(100) + 10;
                Date arrivalDate = new Date();
                String description = "Description for " + productName;

                Product product = new Product(productName, unit, quantity, price, arrivalDate, description);
                store.addProduct(product);
            }
        }
    }

    private static Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            System.err.println("Error parsing date. Using current date.");
            return new Date();
        }
    }

    private static StoreContainer loadStoreContainer() {
        StoreContainer container;

        File file = new File(STORES_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                container = (StoreContainer) ois.readObject();
                System.out.println("Data loaded successfully from " + STORES_FILE);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading data from " + STORES_FILE + ". Starting with an empty container.");
                container = new StoreContainer();
            }
        } else {
            System.out.println(STORES_FILE + " not found. Starting with an empty container.");
            container = new StoreContainer();
        }

        return container;
    }

    private static void saveStoreContainer(StoreContainer storeContainer) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORES_FILE))) {
            oos.writeObject(storeContainer);
            System.out.println("Data successfully saved to file " + STORES_FILE);
        } catch (IOException e) {
            System.err.println("Error saving data to file: " + e.getMessage());
        }
    }
}