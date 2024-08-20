# Hardware Design - CHISEL

## Arithmetic Circuits in CHISEL
The skeleton files for this part of the project are in the folder ```src/main/scala/arithmetic``` and the corresponding tests are in the folder ```src/test/scala/arithmetic```.

### Division Circuit

Integer division of two n-bit unsigned binary numbers A and B can be implemented as a sequential circuit, based on the [grade school division method](https://en.wikipedia.org/wiki/Long_division). The division
⟨A⟩ according to the school method can be expressed algorithmically as follows:
```
for i = n-1 to 0
  R’ = 2 * R + A[i]
  if (R’ < B) then Q[i] = 0, R = R’
              else Q[i] = 1, R = R’-B
```

At the end of the loop, ```Q``` holds the quotient and ```R``` the remainder of the division. <br>
The division circuit has two ```n-bit``` inputs ```dividend``` and ```divisor```, a boolean input ```start```, an implicit clock input, two ```n-bit``` outputs ```quotient``` and ```remainder```, and a boolean output ```done```. <br><br>
The computation should start when start is set to ```true```. n clock cycles later the outputs quotient and remainder should contain the correct results and done should be set to true until the next division is initiated. Whenever start = 1 occurs the circuit should start over with the current input values. The circuit should ignore changes to inputs other than start after a computation has been started. In the following section, we provide some additional guidance.
