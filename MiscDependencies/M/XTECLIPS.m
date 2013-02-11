XTECLIPS	;JLI/FO-OAK - Eclipse Interface Via VistA Link ;01/15/12  13:56
	;;7.3;TOOLKIT;**101**;Apr 25, 1995
	;;Per VHA Directive 2004-038, this routine should not be modified
	; JLI 120115 added an extra argument to list to prevent occasional errors reported on GT.M
RPC(XTECRES,XTECFUNC,XTECLINE,XTECFROM,XTECTO,XTECOPT,XTECXTRA)	;
	S ^XTMP("XTECLIPS",0)=3120211
	S ^XTMP("XTECLIPS",$H,1,"XTECFUNC")=$G(XTECFUNC)
	I $D(XTECLINE)>0 M ^XTMP("XTECLIPS",$H,2,"XTECLINE")=XTECLINE
	I $D(XTECFROM)>0 M ^XTMP("XTECLIPS",$H,3,"XTECFROM")=XTECFROM
	I $D(XTECTO)>0 M ^XTMP("XTECLIPS",$H,4,"XTECTO")=XTECTO
	I $D(XTECOPT)>0 M ^XTMP("XTECLIPS",$H,5,"XTECOPT")=XTECOPT
	; XTECRES = this is the return value from the RPC call, will contain a global reference
	; XTECFUNC = this is the type of function that is being done.
	;       = RD Routine directory passed back in ARRAY
	;       = RL Routine Load into ARRAY
	;       = RS Routine Save Save the routine from XTECLINE
	;       = GD Global directory Passed Back in ARRAY
	;       = GL Global List in ARRAY
	; XTECLINE = This is the total number of line that are requested at a time
	; XTECFROM = This is the starting point or the one to be listed
	; XTECTO =  this is the ending point
	; XTECXTRA = an extra argument to prevent occasional errors in GT.M 120115 JLI
	N TMPGLOB
	S TMPGLOB=$NA(^TMP("XTECLIPS",$J)) K @TMPGLOB
	S XTECRES=TMPGLOB
	S XTECFUNC=$G(XTECFUNC),XTECFROM=$G(XTECFROM),XTECTO=$G(XTECTO)
	;
	I XTECFUNC="RD" D DIR^XTECROU(TMPGLOB,$G(XTECLINE),XTECFROM,XTECTO) Q
	I XTECFUNC="RL" D LOAD^XTECROU(TMPGLOB,XTECFROM) Q
	I XTECFUNC="RS" D SAVE^XTECROU(TMPGLOB,.XTECLINE,XTECFROM,XTECTO) Q
	I XTECFUNC="RI" D INFO^XTECROU(TMPGLOB,XTECFROM,XTECTO) Q
	I XTECFUNC="GD" D LIST^XTECGLO(TMPGLOB,$G(XTECLINE),XTECFROM,XTECTO) Q
	I XTECFUNC="GL" D LNODE^XTECGLO(TMPGLOB,$G(XTECLINE),XTECFROM,$G(XTECTO),$G(XTECOPT)) Q
	; 101105 return whether production system or not.
	I XTECFUNC="PROD" S @TMPGLOB@(0)=$$PROD^XUPROD Q
	;
	S @TMPGLOB@(0)="-1^INVALID FUNCTION"
	Q
