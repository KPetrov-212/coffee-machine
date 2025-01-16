package main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CoffeeMachine {
    private static final String COFFEE_KEY = "coffee";
    private static final String MILK_KEY = "milk";
    private static final String CHOCOLATE_KEY = "chocolate";
    private static final String ESPRESSO_KEY = "espresso";
    private static final String LATTE_KEY = "latte";
    private static final String MOCHA_KEY = "mocha";

    private int coffee;
    private int milk;
    private int chocolate;
    private int coins;
    private int[] prices;

    public CoffeeMachine() {
        loadIngredients();
        loadPrices();
        this.coins = 0;
    }

    public String getDrinksPrices() {
        return "Espresso: " + prices[0] + ", Latte: " + prices[1] + ", Mocha: " + prices[2];
    }

    public int getCoins() {
        return this.coins;
    }

    public String getIngredients() {
        return "Coffee: " + this.coffee + ", Milk: " + this.milk + ", Chocolate: " + this.chocolate;
    }

    private void loadIngredients() {
        JSONObject jsonObject = parseJsonFile("src/resources/ingredients.json");
        if (jsonObject != null) {
            this.coffee = getIntValue(jsonObject, COFFEE_KEY);
            this.milk = getIntValue(jsonObject, MILK_KEY);
            this.chocolate = getIntValue(jsonObject, CHOCOLATE_KEY);
        }
    }

    private void loadPrices() {
        JSONObject jsonObject = parseJsonFile("src/resources/prices.json");
        if (jsonObject != null) {
            this.prices = new int[3];
            this.prices[0] = getIntValue(jsonObject, ESPRESSO_KEY);
            this.prices[1] = getIntValue(jsonObject, LATTE_KEY);
            this.prices[2] = getIntValue(jsonObject, MOCHA_KEY);
        }
    }

    private JSONObject parseJsonFile(String filePath) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {
            return (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            System.out.println("Error loading JSON file: " + e.getMessage());
            return null;
        }
    }

    private int getIntValue(JSONObject jsonObject, String key) {
        Long value = (Long) jsonObject.get(key);
        return value != null ? value.intValue() : 0;
    }

    private void saveIngredients() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(COFFEE_KEY, this.coffee);
        jsonObject.put(MILK_KEY, this.milk);
        jsonObject.put(CHOCOLATE_KEY, this.chocolate);

        try (FileWriter file = new FileWriter("src/resources/ingredients.json")) {
            file.write(jsonObject.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.out.println("Error saving ingredients: " + e.getMessage());
        }
    }

    public void addCoins(String coinType, int quantity) {
        int coinValue = 0;
        switch (coinType) {
            case "5":
                coinValue = 5;
                break;
            case "10":
                coinValue = 10;
                break;
            case "20":
                coinValue = 20;
                break;
            case "50":
                coinValue = 50;
                break;
            case "1lv":
                coinValue = 100;
                break;
            case "2lv":
                coinValue = 200;
                break;
            default:
                System.out.println("Invalid coin type. Please insert 5, 10, 20, 50, 1lv, or 2lv coins.");
                return;
        }
        this.coins += coinValue * quantity;
    }

    public boolean makeCoffee(String type) {
        int cost = 0;
        switch (type.toLowerCase()) {
            case "espresso":
                cost = prices[0];
                if (coffee >= 50 && coins >= cost) {
                    coffee -= 50;
                    coins -= cost;
                    saveIngredients();
                    return true;
                } else {
                    printInsufficientIngredients(coffee < 50, false, false, coins < cost);
                }
                break;
            case "latte":
                cost = prices[1];
                if (coffee >= 30 && milk >= 20 && coins >= cost) {
                    coffee -= 30;
                    milk -= 20;
                    coins -= cost;
                    saveIngredients();
                    return true;
                } else {
                    printInsufficientIngredients(coffee < 30, milk < 20, false, coins < cost);
                }
                break;
            case "mocha":
                cost = prices[2];
                if (coffee >= 30 && milk >= 20 && chocolate >= 10 && coins >= cost) {
                    coffee -= 30;
                    milk -= 20;
                    chocolate -= 10;
                    coins -= cost;
                    saveIngredients();
                    return true;
                } else {
                    printInsufficientIngredients(coffee < 30, milk < 20, chocolate < 10, coins < cost);
                }
                break;
            default:
                System.out.println("Unknown coffee type.");
                return false;
        }
        return false;
    }
    
    private void printInsufficientIngredients(boolean coffee, boolean milk, boolean chocolate, boolean coins) {
        if (coffee) {
            System.out.println("Not enough coffee.");
        }
        if (milk) {
            System.out.println("Not enough milk.");
        }
        if (chocolate) {
            System.out.println("Not enough chocolate.");
        }
        if (coins) {
            System.out.println("Not enough coins.");
        }
    }

    public void refillIngredients(int coffee, int milk, int chocolate) {
        this.coffee += coffee;
        this.milk += milk;
        this.chocolate += chocolate;
        saveIngredients();
    }

    public List<String> getChange(int amount) {
        int[] coinTypes = {200, 100, 50, 20, 10, 5};
        List<String> change = new ArrayList<>();
        for (int coin : coinTypes) {
            int count = 0;
            while (amount >= coin) {
                amount -= coin;
                count++;
            }
            if (count > 0) {
                String coinType;
                switch (coin) {
                    case 200:
                        coinType = "2lv";
                        break;
                    case 100:
                        coinType = "1lv";
                        break;
                    default:
                        coinType = String.valueOf(coin);
                        break;
                }
                change.add(count + " x " + coinType);
            }
        }
        this.coins = 0; // Reset coins after giving change
        return change;
    }
}