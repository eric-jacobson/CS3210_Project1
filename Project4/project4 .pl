rowBelow( [A,B], [K] ):- K is B-A.
rowBelow( [A,B|T], [K|W] ):-  K is B-A,  rowBelow( [B|T], W).

myLast([A], A).
myLast([A | T], B) :- myLast(T, B).


nextItem([A], A).
nextItem(B, N):- myLast(B, K), rowBelow(B, W), nextItem(W, M), N is M + K.
