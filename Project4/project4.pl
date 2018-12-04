%Project4

%rowBelow( A, B)
%	meaning that A is a list of numbers and B is the row below it in the chart
rowBelow( [A,B], [K] ):- K is B-A.
rowBelow( [A,B|T], [K|W] ):-  K is B-A,  rowBelow( [B|T], W).

%myLast( A, X)
%	meaning that X is the last item in the list A
myLast([A], A).
myLast([A | T], B) :- myLast(T, B).

%nextItem( list, N)
%	where N is the next produced number in accordance with the list
nextItem([A], A).
nextItem(B, N):- myLast(B, K), rowBelow(B, W), nextItem(W, M), N is M + K.