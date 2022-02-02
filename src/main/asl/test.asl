main(fish).
main(meat).
soup(veg).
soup(fish).
wine(white).
wine(red).
restaurant(french).
restaurant(italian).
at(french).

meal(S,M,W) :- soup(S) && main(M) && wine(W).

meal(S,meat,W) >> meal(S,fish,W) :- at(italian).
meal(S,fish,W) >> meal(S,meat,W) :- at(french).
meal(fish,meat,W) >> meal(veg,meat,W) :- true.
meal(veg,fish,W) >> meal(fish,fish,W) :- true.
meal(veg,M,red) >> meal(veg,M,_) :- true.
meal(fish,M,white) >> meal(fish,M,_) :- true.
+!order(M1) >> +!order(M2) :- M1 >> M2.
+!go_order(L,_) >> +!go_order(_,_) :- at(L).
+!go_order(italian, meal(S,meat,W)) >> +!go_order(L,meal(S,_,W)) :- not at(L).

@preferences
+!go_order(Loc,Meal) :
    restaurant(Loc) && not at(Loc) =>
        #println("moving to: " + Loc);
        -at(_);
        +at(Loc);
        !order(Meal).

@preferences
+!go_order(Loc,Meal) :
    restaurant(Loc) && at(Loc) =>
        #println("already at: " + Loc);
        !order(Meal).

@preferences
+!order(meal(S,M,W)) :
    meal(S,M,W) && at(Loc) =>
        #println("ordering: " + meal(S,M,W));
        #coms.achieve(#executionContext.src,meal_from(loc(Loc),meal(S,M,W))).