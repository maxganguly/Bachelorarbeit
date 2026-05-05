# Bachelorarbeit
Ein Repository um den Verlauf meiner Bachelorarbeit und ebendiese zu dokumentieren.

### TODO

+ [ ] Prototyp AST
  + [x] AST Generation
  + [x] AST Vergleich
    + [x] Add Grouping, ordering (ordered, unordered) and comparision modi (none,all,any)
    + [x] Generate Testcases
      + [x] Crude
      + [ ] Improve
    + [x] Error messages 
      + [ ] ?Improve
    + [x] Score tests
+ [ ] MC/DC generate Coverage testcases
  + [ ] Get all Conditions
  + [ ] Generate Testcases based on conditions

+ [ ] Mutation Testing to check coverages
+ [x] Dynamic Execution
  + [x] Run Methods dynamically
  + [x] Run Testcases
    + [x] Validate (True/False)
    + [x] Evaluate (What went wrong)
      + [ ] ?Improve
  + [x] Score Testcases
+ [x] Testcases
  + [x] Generalize Testcases
  + [x] evaluate multiple static&dynamic testcases
+ [x] Document all currently available Code
+ [ ] Evaluation Output
+ [x] Simple Input
+ [x] Multiple Tests to read in 
+ [ ] Papers Lesen
+ [ ] Cleanup Code

## Struktur

In dem File autograder.properties 

### Autograder.properties

+ PrintAllTests=True

  Sollen die Resultate aller Tests (successfull|failed) ausgegeben werden oder nur die fehlgeschlagenen

+ ResultOutputDir=./results/

  Ordner in welchem die Resultate der Tests geschrieben werden

+ SaveTestcases=True

  Sollen die generierten Testcases persistent gespeichert werden.

+ SolutionInputDir=./solution

  src Verzeichnis der Musterlösung

+ Testcases=./testcases

  Verzeichnis in welchen die Testfälle abgespeichert werden

+ ToTestInputDirs=./test

  Verzeichnis aller zu überprüfenden Abgaben. Erwartet den Namen `Matrikelnummer_<Name unterteilt mit _ >` oder `Matrikelnummer_<Name unterteilt mit _ >`

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
| visitMemberReference                                         | Nicht notwendig da kein OOP                                  |
| visitPrimitiveType, <br />visitArrayType                     | Sollte bereits in Variable definiert sein                    |
| visitParameterizedType,<br />visitUnionType<br />,visitIntersectionType<br />visitTypeParameter, <br />visitWildcard | Es werden keine Generischen Klassen angelegt werden müssen   |
| visitModifiers                                               | Es werden keine neuen Klassen angelegt werden sollen         |
| visitAnnotation,<br />visitAnnotatedType                     | Es müssen keine Annotations verwendet werden                 |
| visitModule,<br />visitExports,<br />visitOpens,<br />visitProvides,<br />visitRequires,<br />visitUses | Package management sollte und muss nicht in diesem Ausmaß betrieben werden. |
| visitOther,<br />visitErroneous                              | Werden nur in javac verwendet                                |
| visitYield                                                   | Sollte nicht verwendet werden                                |
