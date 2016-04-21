@echo off
cd %1
%2 -y -i %3 -vf "movie=%4 [wn];[in][wn] overlay=%5 [out]" %6