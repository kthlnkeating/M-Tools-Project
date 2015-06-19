ERRORTST ;
 ;
ADD(A,B) ;
 N SUM
 S SUM=A+B
 Q SUM
 ;
MULTIPLY(A,B) ;
 N PROD
 S PROD=A*
 Q PROD    ; QUIT
 ;
MAIN
 N A,B
 N C,D
 S C=
 S D=A+B
 S MULTIPLY(C,D)
 W D Q ;
 Q
 ; 
DOERR
 F J=1:1:1 S A=A+1
 . S Y=1
 Q
 ;
DOERR2
 S A=A+1 D
 . S Y=1
 ;
 . S K=3
 Q
 ;
 