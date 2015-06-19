APIROU01 ;AU - API TEST ; JUN 18,2012
 ;;3.0;TESTING;;JUN 18,2012
 ;
 Q
 ;
SUMFACT(N,M)
 N I
 F  D  Q:I>3
 . N R
 . S R=$$FACT^APIROU00(I)+$$SUM^APIROU00(I)
 . S ^UD(5,7)=^UD(N,M)
 . S I=I+1+^UM
 . S P=R
 Q S
 ;
STORE(A) ;
 N A
 G:A>1 STOREG^APIROU00
 N I
 F I=1:1:10 D
 . S A("F")=$$FACT^APIROU00(I)
 . S D=4
 . S A(D)=4
 Q:K>3
 S R=1
 Q
 ;
LOOP(NUM) ;
 F I=1:1:NUM D
 . F J=1:1:NUM D
 . . I A S B=$$SUMFACT(2,3)
 . . S D=$$SUMFACT(4,1)
 . . D LATER(J)
 Q
 ; 
LATER(N) ;
 W C
 Q
 ; 
