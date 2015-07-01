rm main.bbl  main.dvi  main.log  main.out  main.aux  main.blg  main.lof  main.lot  main.toc
pdflatex main.tex
bibtex main.aux
pdflatex main.tex
pdflatex main.tex