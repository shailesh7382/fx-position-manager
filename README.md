# FX Position Manager 

1. Net Position Calculation: Tracks positions at the individual currency level.
2. Valuation in a Base Currency: Converts all positions to a common base currency (e.g., USD) for valuation.
3. Transaction History: Maintains a history of all transactions.
4. Error Handling: Validates transactions and prevents invalid operations.
5. Position Reporting: Provides detailed reports of positions and valuations.
6. Reverse Original Transaction: The effect of the original transaction is reversed in the positions. 
7. Apply Updated Transaction: The updated transaction is applied to the positions. 
8. The original transaction is replaced with the updated one in the transaction history.
   
## Book
Implemented a Book Hierarchy where parent books aggregate positions from their child books 
while still maintaining specific positions for each child book, we can use a composite pattern. 
This pattern allows us to treat individual books and composite books (parent books) uniformly.

In this hierarchy:
1. Ecomm is the parent of Warehouse and AutoHedged.
2. Voice is the parent of Manual, NDF, and Bullion.

Each parent book will aggregate positions from its child books, while each child book maintains its own specific positions.

Cache position values in the base currency to avoid redundant calculations.


## LongAdder
LongAdder is designed for high-concurrency scenarios where multiple threads frequently update a single value.
It uses a striped counter approach to reduce contention between threads.


## Disruptor
The Disruptor is a low-latency, high-throughput messaging framework designed for concurrent systems. 
It uses a ring buffer to pass events between producers and consumers efficiently.
1. Ring Buffer: A circular array that stores events.
2. Event: A data structure representing a transaction or market data update.
3. Producer: Publishes events to the ring buffer.
4. Consumer: Processes events from the ring buffer.


### Correlation Analysis: 
HFT firms often use statistical models to identify correlations between different currency pairs. 
By building positions in correlated currencies, they can construct prices that reflect these relationships. 
For example, if EUR/USD and GBP/USD are highly correlated, the firm might use its position in one pair to infer the likely movement in the other.

### Signal Generation: 
Currency positions can be used to generate trading signals. 
For instance, if a firm has a long position in a currency that is showing strength against a basket of other currencies, 
it might use this signal to adjust its pricing and take advantage of the trend.

### Delta Hedging: 
HFT firms use their currency positions to hedge against adverse movements in exchange rates. 
By constructing a delta-neutral portfolio, they can minimize their exposure to currency risk while still providing liquidity.