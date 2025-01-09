package main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CoffeeMachine {
    private int coffee;
    private int milk;
    private int chocolate;
    private int coins;

    public CoffeeMachine() {
        loadIngredients();
        this.coins = 0;
    }

    public String getDrinksPrices() {
        return "Espresso: 50, Latte: 70, Mocha: 90";
    }

    public int getCoins() {
        return this.coins;
    }

    public String getIngredients() {
        return "Coffee: " + this.coffee + ", Milk: " + this.milk + ", Chocolate: " + this.chocolate;
    }

    private void loadIngredients() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader("src/resources/ingredients.json")) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            this.coffee = ((Long) jsonObject.get("coffee")).intValue();
            this.milk = ((Long) jsonObject.get("milk")).intValue();
            this.chocolate = ((Long) jsonObject.get("chocolate")).intValue();
        } catch (IOException | ParseException e) {
            System.out.println("Error loading ingredients: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void saveIngredients() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("coffee", this.coffee);
        jsonObject.put("milk", this.milk);
        jsonObject.put("chocolate", this.chocolate);

        try (FileWriter file = new FileWriter("src/resources/ingredients.json")) {
            file.write(jsonObject.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.out.println("Error saving ingredients: " + e.getMessage());
        }
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public boolean makeCoffee(String type) {
        int cost = 0;
        switch (type.toLowerCase()) {
            case "espresso":
                cost = 50;
                if (coffee >= 50 && coins >= cost) {
                    coffee -= 50;
                    coins -= cost;
                    saveIngredients();
                    return true;
                }
                break;
            case "latte":
                cost = 70;
                if (coffee >= 30 && milk >= 20 && coins >= cost) {
                    coffee -= 30;
                    milk -= 20;
                    coins -= cost;
                    saveIngredients();
                    return true;
                }
                break;
            case "mocha":
                cost = 90;
                if (coffee >= 30 && milk >= 20 && chocolate >= 10 && coins >= cost) {
                    coffee -= 30;
                    milk -= 20;
                    chocolate -= 10;
                    coins -= cost;
                    saveIngredients();
                    return true;
                }
                break;
            default:
                System.out.println("Unknown coffee type.");
                return false;
        }
        System.out.println("Not enough ingredients or coins.");
        return false;
    }

    public void refillIngredients(int coffee, int milk, int chocolate) {
        this.coffee += coffee;
        this.milk += milk;
        this.chocolate += chocolate;
        saveIngredients();
    }
}