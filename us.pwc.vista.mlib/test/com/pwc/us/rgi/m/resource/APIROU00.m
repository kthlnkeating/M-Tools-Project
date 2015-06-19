APIROU00 ;AU - API TEST ; JUN 18,2012
 ;;3.0;TESTING;;JUN 18,2012
 ;
 Q
 ;
FACT(N) ;
 N R
 S R=1
 F I=1:1:N S R=R*I
 Q R
 ;
SUM(N) ;
 S R=0
 F I=1:1:M S R=R+I
 S ^RGI0("EF")=5
 Q R
 ;
SUMFACT(N,M)
 N I
 F  D  Q:I>3
 . N R
 . S R=$$FACT(I)+$$SUM(I)
 . S I=I+1
 . S P=R
 Q S
 ;
STORE(A) ;
 N I
 F I=1:1:10 D
 . S A("F")=$$FACT(I)
 . S D=4
 . S A(D)=4
 Q:K>3
 S R=1
 Q
 ;
STOREG ;
 N I
 F I=1:1:10 D
 . S A("F")=$$FACT(I)
 . S D=4
 . S A(D)=4
 Q:K>3
 S R=1
 Q
 ;
TOOTHER
 D FACT^APIROU02(5) Q 
TONONE
 D ^APIROU02 
 ;
ZZ S A=A+1
 S D=D+1
