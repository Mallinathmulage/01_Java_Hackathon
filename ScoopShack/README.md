# ScoopShack - Java Hackathon Problem 29
Menu-driven **console** program. Java 17+, **arrays only** (no ArrayList / HashMap), no GUI, no files created.

## 1. How to run
Open a terminal in this folder (the one containing `src`), then:
- Windows: `build.bat`
- Mac/Linux: `chmod +x build.sh` then `./build.sh`

Manual: `javac -d out $(find src -name "*.java")` and `java -cp out com.scoopshack.app.ScoopShackApp`

## 2. Rule-by-rule compliance (PDF -> where it is done)

### Problem statement points
| # | PDF requirement | Done in |
|---|---|---|
| 1 | Menu with id, name, category, price, stock, LOW/OK | `ConsoleUI.showMenu()`, `MenuItem.getStatus()` |
| 2 | Name + type (Regular/Student), ids and quantities, `-1` undo, `0` finish | `ConsoleUI.takeOrder()`, `ItemStack.pop()` |
| 3 | Reject bad id / too much stock, keep running, no stock change | `ShopService.addToCart()` only *checks*; `checkout()` validates then reduces |
| 4 | Order id ICE1.., token, odd = Window 1, even = Window 2, bill with discount, GST, total | `Order` constructor, `Order.getWindow()`, `Order.toBill()` |
| 5 | Find old bill by order id | `ConsoleUI.viewBill()`, `ShopService.findOrder()` |
| 6 | Report: total orders, revenue, most popular item | `ConsoleUI.showReport()` |
| 7 | Never crash on wrong input | `InputHelper` (every read loops until valid) |

### "What your code must use"
| Unit | Requirement | Where |
|---|---|---|
| I | `MenuItem[]`, `Order[]` | `ShopService` fields `menu`, `orders` |
| I | int, double, char, boolean | `char type` (ConsoleUI), `boolean finished`, prices as `double` |
| I | `final double GST = 0.18` | `Order.GST` (`public static final double`) |
| II | do-while + switch | `ConsoleUI.run()` |
| II | `?:` for LOW (below 10) / OK | `MenuItem.getStatus()` |
| II | `token & 1` | `Order.getWindow()` |
| III | Constructors with `this` | every model class, `ItemStack()` calls `this(8)` |
| III | One overloaded method | `Order.getSubtotal()` / `getSubtotal(int)` (also `ShopService.findItem(int)` / `findItem(String)`) |
| III | Static counter ICE1, ICE2 | `Order.orderCounter`, `Order.tokenCounter` |
| III | Recursive subtotal | `Order.getSubtotal(int index)` |
| III | Own `ItemStack` for undo | `model/ItemStack.java` (array-based push/pop) |
| IV | Packages | `app, exceptions, model, service, ui, util` |
| IV | Person -> abstract Customer -> Regular / Student with `super` | `model/` |
| IV | Overridden `getDiscountRate()` | `Regular` (0.0), `Student` (0.05) |
| IV | `Billable` interface | `model/Billable.java`, implemented by `Order` |
| IV | One `final` method | `Customer.getTypeLabel()` |
| IV | `toString()` | Person, Customer, MenuItem, OrderLine, Order |
| V | Checked exceptions | `MenuItemNotFoundException`, `InsufficientStockException` (both extend `Exception`) |
| V | `throw`, `throws` | `findItem` / `addToCart` / `checkout` |
| V | Multiple catch | `takeOrder()` catches 3 exception types |
| V | Nested try | inner try per line + try for final checkout, inside the outer session try |
| V | `finally` | `takeOrder()` prints "session closed" always |
| V | try-with-resources | `ScoopShackApp.main` (Scanner) |

### Billing and menu data
Subtotal - discount (Regular 0%, Student 5%) + 18% GST on the discounted amount, 2 decimals. Menu ids 101-106
with the exact names, categories, prices and stock from the PDF. Money prints as `Rs.` (the PDF's rupee sign can
show as `?` on some Windows consoles). To switch, change the text in `util/Money.java` and `Order.toBill()`.

## 3. Test cases (type exactly this at the program)
| Case | Keystrokes | Expected |
|---|---|---|
| 1 | `2`, `Asha`, `R`, `101`, `2`, `105`, `1`, `0` | Final total **354.00**, Token 1, Window 1 |
| 2 | `2`, `Ravi`, `S`, `102`, `3`, `106`, `2`, `0` | Subtotal 1150.00, discount 57.50, GST 196.65, total **1289.15** |
| 3 | `2`, `Meena`, `R`, `102`, `21`, `0` | InsufficientStockException message; option `1` shows Sundae stock still 20 |
| 4 | `2`, `Tom`, `R`, `999`, `1`, `0` | MenuItemNotFoundException message; program continues |
Undo: in an order enter `101`, `3` then `-1` - the line is removed. Then `3` + `ICE1` shows an old bill, `4` the report, `5` exits.

## 4. Class hierarchy
```
Person (name)
  +-- Customer (abstract: getDiscountRate(); final getTypeLabel())
        +-- Regular -> 0%
        +-- Student -> 5%
Billable (interface) <- implemented by Order
```

## 5. What happens in "New order" (step by step)
1. Ask name and type; `R` creates a `Regular`, `S` a `Student` (variable type is `Customer` - polymorphism).
2. Loop asking item id. `0` = finish, `-1` = pop the last line from the `ItemStack`.
3. Otherwise ask quantity and call `addToCart`: find item (may throw not-found), reject quantity < 1, add what is
   already in the cart and compare with stock using `long` (may throw insufficient stock). Stock is NOT reduced yet.
4. On finish, `checkout` validates every line again, then reduces stock, then creates the `Order`
   (only now do the static counters give ICE id and token), and stores it in `Order[]`.
5. The bill prints with items, discount, GST and final total.

## 6. Checkpoint prep
- **CP1 data types / casting / arrays:** `(long) alreadyInCart + quantity`, `(double) orders`, `(int) Math.round(rate * 100)`; menu in `MenuItem[]`.
- **CP2 operators / loops / switch:** `&` works on bits and does not short-circuit, `&&` is logical and stops early; `token & 1` is 1 for odd numbers.
- **CP3 static / this / overloading / recursion / stack:** one shared counter for all orders; `getSubtotal(index)` = line total + `getSubtotal(index + 1)`, stops at `index >= size`; stack = last in, first out.
- **CP4 super / overriding / abstract vs interface / final:** abstract class holds state and shared code ("is-a"); interface is a pure capability ("can be billed"); `final` stops overriding.
  Surprise-change idea (Staff 10%): add `Staff extends Customer` returning 0.10, then add `'F'` in `InputHelper.readCustomerType` and `ConsoleUI.takeOrder`.
- **CP5 exceptions:** `throw` raises one, `throws` declares it; `finally` always runs; checked exceptions must be caught or declared.

## 7. Before submitting
Edit `AI_LOG.txt` honestly (what you asked, what you changed). Each teammate must be able to explain every file.
