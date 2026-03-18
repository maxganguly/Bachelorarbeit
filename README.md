# Bachelorarbeit
Ein Repository um den Verlauf meiner Bachelorarbeit und ebendiese zu dokumentieren

### TODO

+ [ ] Prototyp AST
  + [x] AST Generation
  + [x] AST Vergleich
    + [ ] Error messages 
+ [ ] Prototyp strongest Postcondition
  + [ ] SP berechnen
    + [ ] Side effects?
  + [ ] Generate Testcases
+ [ ] Papers Lesen



### Overleaf

Der geschriebene Teil ist unter [https://www.overleaf.com/read/btwrzntmdvjs#aa6c3d](https://www.overleaf.com/read/btwrzntmdvjs#aa6c3d) aufzufinden.



### AST

Die Generation eines AST wird durch [ASTScannerText.java](./src/main/ASTScannerText.java) definiert. Einige Tags werden nicht generiert da diese für diesen Kurs und daher die Überprüfung nicht erforderlich sind.

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
