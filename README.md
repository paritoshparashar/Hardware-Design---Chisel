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
