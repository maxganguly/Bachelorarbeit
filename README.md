# Bachelorarbeit
Ein Repository um den Verlauf meiner Bachelorarbeit und ebendiese zu dokumentieren

### TODO

+ [ ] Prototyp AST
  + [x] AST Generation
  + [x] AST Vergleich
    + [ ] Generate Testcases
    + [ ] Error messages 
    + [ ] Score tests
+ [ ] Prototyp strongest Postcondition
  + [ ] SP berechnen
    + [ ] Side effects?
  + [ ] Generate Testcases
+ [ ] Dynamic Execution
  + [x] Run Methods dynamically
  + [ ] Run Testcases
    + [x] Validate (True/False)
    + [ ] Evaluate (What went wrong)
  + [ ] Score Testcases
+ [ ] Testcases
  + [ ] Geberalize Testcases
  + [ ] evaluate multiple static&dynamic testcases

+ [ ] Document all currently available Code
+ [ ] Papers Lesen



### Overleaf

Der geschriebene Teil ist unter [https://www.overleaf.com/read/btwrzntmdvjs#aa6c3d](https://www.overleaf.com/read/btwrzntmdvjs#aa6c3d) aufzufinden.

### Dynamic Execution

Testfälle können im Schema `returntyp methodenname(parametertyp parameterwert)` mit beliebig vielen mit `,` getrennten Variablentupeln beschrieben werden.

Arrays werden mittels `arraytyp[] {v1,v2,v3}` (für 1 dimensionale Arrays)  angeschrieben.

Momentan können nur bis zu 4-dimensionale Arrays eingelesen und bis zu 3-dimensionale Arrays ausgelesen werden. 

### AST

Die Generation eines AST wird durch [ASTScannerText.java](./src/main/ASTScannerText.java) und [ASTTreeScanner.java](./src/main/ASTTreeScanner.java) definiert. Einige Tags werden nicht generiert da diese für diesen Kurs und daher die Überprüfung nicht erforderlich sind.

Folgende Teile werden nicht in den AST übernommen:

 

| Tagname                                                      | Grund des Ausschlusses                                       |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| visitNewClass                                                | Nicht benötigt wird als Methodenaufruf behandelt             |
| visitParenthesized                                           | Es werden keine generischen Objekte verwendet                |
| visitInstanceOf, <br />visitBindingPattern                   | Es sollte nicht mit Polymorphie gearbeitet werden            |
| visitAnyPattern, <br />visitStringTemplate                   | Preview nicht entschieden, vielleicht benötigt               |
| visitDefaultCaseLabel, <br />visitConstantCaseLabel          | Label in diesem Ausmaß nicht erforderlich                    |
| visitDeconstructionPattern                                   | Sollte nicht verwendet werden können da die erlaubten Klassen beschränkt sind |
| visitArrayAccess                                             | Möglicherweise notwendig, sehr spezifisch (zu genau)         |
| visitMemberSelect,<br />visitMemberReference                 | Nicht notwendig da kein OOP                                  |
| visitPrimitiveType, <br />visitArrayType                     | Sollte bereits in Variable definiert sein                    |
| visitParameterizedType,<br />visitUnionType<br />,visitIntersectionType<br />visitTypeParameter, <br />visitWildcard | Es werden keine Generischen Klassen angelegt werden müssen   |
| visitModifiers                                               | Es werden keine neuen Klassen angelegt werden sollen         |
| visitAnnotation,<br />visitAnnotatedType                     | Es müssen keine Annotations verwendet werden                 |
| visitModule,<br />visitExports,<br />visitOpens,<br />visitProvides,<br />visitRequires,<br />visitUses | Package management sollte und muss nicht in diesem Ausmaß betrieben werden. |
| visitOther,<br />visitErroneous                              | Werden nur in javac verwendet                                |
| visitYield                                                   | Sollte nicht verwendet werden                                |
