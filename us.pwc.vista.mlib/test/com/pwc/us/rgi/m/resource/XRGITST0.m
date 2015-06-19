XRGITST0 ;
 Q
 ;
ADD(A,B,C) ;
 S A=B+C
 Q
 ;
SUB(A,B,C)
 S A=B-C
 Q
 ;
TAG1 ;
 N A,B,C
 S A=1,B=1,C=1
 F I=1:1 D ADD:A>5,SUB:A>10 D  Q:A>20  G TAG1
 . S A=1
 G TAG3:A=3,@("TAG"_B):C'>3,@A^@B
 D @C^@B,@B
 Q
 ;
TAG2 ;
 F I=1:1 D ADD:A>5,SUB:A>10 D  Q:A>20  G TAG1
 . ; Empty line
 . S A=1
 Q
 ;
TAG3 N A S A=1 D:TAG1 ADD(A,1,1),SUB(A,1,1) Q
