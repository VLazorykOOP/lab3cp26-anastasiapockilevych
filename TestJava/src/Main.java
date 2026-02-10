import java.util.ArrayList;
import java.util.List;

interface ItemComponent {
    double getPrice();
    void printName();
}

class Product implements ItemComponent {
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void printName() {
        System.out.println("  - Товар: " + name + " ($" + price + ")");
    }
}

class Box implements ItemComponent {
    private List<ItemComponent> items = new ArrayList<>();
    private String name;

    public Box(String name) {
        this.name = name;
    }

    public void addItem(ItemComponent item) {
        items.add(item);
    }

    @Override
    public double getPrice() {
        double totalPrice = 0;
        for (ItemComponent item : items) {
            totalPrice += item.getPrice();
        }
        return totalPrice;
    }

    @Override
    public void printName() {
        System.out.println("Box [" + name + "] contains:");
        for (ItemComponent item : items) {
            item.printName();
        }
    }
}

interface Transport {
    void deliver();
}

class Truck implements Transport {
    @Override
    public void deliver() {
        System.out.println("🚚 Доставка вантажівкою (наземний транспорт).");
    }
}

class Drone implements Transport {
    @Override
    public void deliver() {
        System.out.println("🚁 Доставка дроном (повітряний експрес).");
    }
}

abstract class Logistics {
    public abstract Transport createTransport();

    public void startDelivery() {
        Transport transport = createTransport();
        transport.deliver();
    }
}

class RoadLogistics extends Logistics {
    @Override
    public Transport createTransport() {
        return new Truck();
    }
}

class AirLogistics extends Logistics {
    @Override
    public Transport createTransport() {
        return new Drone();
    }
}

interface OrderState {
    void pay(Order context);
    void ship(Order context);
    void deliver(Order context);
}

class Order {
    private OrderState state;
    private ItemComponent rootItem;
    private Logistics logistics;

    public Order(ItemComponent rootItem, Logistics logistics) {
        this.rootItem = rootItem;
        this.logistics = logistics;
        this.state = new NewState();
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public Logistics getLogistics() {
        return logistics;
    }

    public void proceedToPayment() {
        state.pay(this);
    }

    public void proceedToShipping() {
        state.ship(this);
    }

    public void proceedToDelivery() {
        state.deliver(this);
    }

    public void printOrderDetails() {
        System.out.println("\n=== Деталі замовлення ===");
        rootItem.printName();
        System.out.println("ЗАГАЛЬНА СУМА: $" + rootItem.getPrice());
        System.out.println("=========================");
    }
}

class NewState implements OrderState {
    @Override
    public void pay(Order context) {
        System.out.println("✅ Оплата пройшла успішно.");
        context.setState(new PaidState());
    }

    @Override
    public void ship(Order context) {
        System.out.println("❌ Помилка: Не можна відправити неоплачене замовлення!");
    }

    @Override
    public void deliver(Order context) {
        System.out.println("❌ Помилка: Замовлення ще навіть не оплачене.");
    }
}

class PaidState implements OrderState {
    @Override
    public void pay(Order context) {
        System.out.println("⚠️ Замовлення вже оплачене.");
    }

    @Override
    public void ship(Order context) {
        System.out.println("📦 Замовлення передається у службу доставки...");
        context.getLogistics().startDelivery();
        context.setState(new ShippedState());
    }

    @Override
    public void deliver(Order context) {
        System.out.println("❌ Помилка: Спочатку треба відправити замовлення.");
    }
}

class ShippedState implements OrderState {
    @Override
    public void pay(Order context) {
        System.out.println("❌ Помилка: Замовлення вже в дорозі.");
    }

    @Override
    public void ship(Order context) {
        System.out.println("⚠️ Замовлення вже відправлено.");
    }

    @Override
    public void deliver(Order context) {
        System.out.println("🎁 Замовлення успішно вручено клієнту!");
        context.setState(new DeliveredState());
    }
}

class DeliveredState implements OrderState {
    @Override
    public void pay(Order context) {
        System.out.println("ℹ️ Замовлення закрито.");
    }

    @Override
    public void ship(Order context) {
        System.out.println("ℹ️ Замовлення вже у клієнта.");
    }

    @Override
    public void deliver(Order context) {
        System.out.println("ℹ️ Замовлення вже доставлено.");
    }
}

public class Main {
    public static void main(String[] args) {
        Product phone = new Product("iPhone 15", 1000);
        Product charger = new Product("Charger", 50);
        Product headphones = new Product("AirPods", 200);

        Box accessoriesBox = new Box("Аксесуари");
        accessoriesBox.addItem(charger);
        accessoriesBox.addItem(headphones);

        Box mainParcel = new Box("Головна посилка");
        mainParcel.addItem(phone);
        mainParcel.addItem(accessoriesBox);

        Logistics selectedLogistics = new AirLogistics();

        Order order = new Order(mainParcel, selectedLogistics);

        order.printOrderDetails();

        System.out.println("\n--- Початок обробки ---");

        order.proceedToShipping();

        order.proceedToPayment();

        order.proceedToShipping();

        order.proceedToDelivery();

        order.proceedToShipping();
    }
}
