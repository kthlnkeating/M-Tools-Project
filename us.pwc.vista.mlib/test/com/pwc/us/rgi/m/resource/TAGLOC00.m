TAGLOC00 ;
 Q
 ;
E0 ;
 N I,SUM
 F I=1:5:1 D
IN0 . S SUM=SUM+I
 . I SUM>5 D
 . . S SUM=SUM-1 N J
IN1 . . S J=$G(J)+1 Q:J>4
 . . S SUM=SUM+1
 . . G IN1
IN2 . S SUM=SUM+3
 . G:SUM<100 IN2
 Q
 ;
E1 ;
 N N,SUM
 F  S N=$O(^X(N)),SUM=SUM+N Q:SUM>5
 Q
 ; 
E2 ;
 N N,SUM
 F  S N=$O(^X(N)),SUM=SUM+N Q:SUM>5
 ;
E3
 Q
 ;
