XTMUNIT1 ;JLI/FO-OAK-CONTINUATION OF UNIT TEST ROUTINE ;08/13/12  10:29
 ;;7.3;TOOLKIT;**81**;APR 25 1995;Build 24
 ;;Per VHA Directive 2004-038, this routine should not be modified
 D EN^XTMUNIT("ZZUTXTMU")
 Q
 ;
CHEKTEST(ROU,XTMUNIT,XTMUETRY) ; CHECK FOR @TEST ON TAG LINE TO INDICATE A UNIT TEST ENTRY
 ; XTMROU - input - Name of routine to check for tags with @TEST attribute
 ; XTMUNIT - input/output - passed by reference
 ; XTMUETRY - input/output - passed by reference
 ; get routine code into a location to check it
 N CNT,LN,I,DIF,X,XCNP,TMP,LINE
 S I=$$SETNAMES^XTECGLO(ROU,"") I I<0 Q "-1^Invalid Routine Name"
 ; $$ROU(ROU) used a check of the ROUTINE file for file name
 ; but routines with names longer than the standard always
 ; show up as not found will trap the error instead if not present
 ; I '$$ROU(ROU) Q "-1^Routine Not found" ; JLI 120806
 N $ETRAP S $ETRAP="D CATCHERR^XTMUNIT1"
 S DIF="TMP(",XCNP=0,X=ROU
 X ^%ZOSF("LOAD")
 I '$D(TMP(1,0)) Q
 F I=1:1 Q:'$D(TMP(I,0))  S LINE=TMP(I,0) I $E(LINE)'=" ",$$UP^XLFSTR(LINE)["@TEST" D
 . N TAGNAME,CHAR,NPAREN S TAGNAME="",NPAREN=0
 . F  Q:LINE=""  S CHAR=$E(LINE),LINE=$E(LINE,2,999) Q:CHAR=""  Q:" ("[CHAR  S TAGNAME=TAGNAME_CHAR
 . ; should be no paren or arguments
 . I CHAR="(" Q
 . F  Q:LINE=""  S CHAR=$E(LINE) Q:" ;"'[CHAR  S LINE=$E(LINE,2,999)
 . I $$UP^XLFSTR($E(LINE,1,5))="@TEST" S LINE=$E(LINE,6,999) D
 . . S XTMUNIT("ENTN")=XTMUNIT("ENTN")+1,XTMUETRY(XTMUNIT("ENTN"))=TAGNAME
 . . F  Q:LINE=""  S CHAR=$E(LINE) Q:CHAR?1AN  S LINE=$E(LINE,2,999)
 . . S XTMUETRY(XTMUNIT("ENTN"),"NAME")=LINE
 . . Q
 . Q
 Q
 ;
CATCHERR ; catch error on trying to load file if it doesn't exist ; JLI 120806
 S $ZE="",$EC=""
 Q
