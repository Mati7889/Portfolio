# Lotto
This object-oriented project emphasizes a clean class hierarchy, advanced error handling, and ensures correct interaction between components.
It is designed to maintain high performance even during draws involving thousands of players and tickets.
The project also demonstrates domain modeling, providing a clear representation of the main entities and their interactions within the lottery system.

 ## It covers a complete simulation of a lottery system, including:

- **Institution system**: a cooperative structure involving the State Budget, the Headquarters, and multiple Offices.
- **Tax collection** from prize winnings.  
- **Subsidies** provided by the state budget to support the lottery office when funds are insufficient.  
- **Prize pool management**: calculation of rewards, distribution among tiers, and jackpot rollovers.  
- **Lottery draws**: random selection of 6 out of 49 numbers.  
- **Ticket simulation**: players can purchase two types of customizable tickets (randomized and self-filled).  
- **Player strategies**: four different approaches to filling tickets (minimalist, random, fixed numbers, fixed blank).  
- **Payout processing**: validating winning tickets and distributing prizes.  
- **Final report**: printing detailed lottery results after all draws.

## Error Handling
The project includes advanced error-handling mechanisms:  
- Validation of ticket data (e.g., invalid numbers, incorrect formats).  
- Detection of inconsistent system states (e.g., negative balances, invalid payouts).  
- Safe handling of exceptional situations in financial calculations.  
- Informative error messages for debugging and testing purposes.  

## Class Reference
**[Presentation](./Presentation.java)** - runs the simulation for 20 draws, 10 offices and 200 players each strategy.

**[Ticket](./Ticket)** - each has its own ID, from 1 to 8 six digit bets, can be bought for up to the 10 next draws and validated in office of purchase.

**[Headquarters](./Headquarters)** - class responsible for carrying out the draws, calculating pots and jackpots.

**[Collection Office](./CollectionOffice)** - sells and validates lottery tickets, serves as the prize collection point and allows players to buy multiple ticket variations.

**[State](./StateBudget)** - gives subsidies if needed, collects taxes, gives reports.

**[Minimalist](./Minimalist)** - buys one randomized ticket valid only for the nearest draw.

**[Fixed Form](./FixedForm)** - always uses their favorite form and buys tickets at fixed intervals.

**[Fixed Number](./FixedNumber)** - always bets one set of numbers for the next 10 draws.

**[Random](./Random)** - picks their tickets at random.
