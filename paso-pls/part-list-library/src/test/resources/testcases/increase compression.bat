set ZIP7="C:\Program Files\7-Zip\7z.exe"

for %%z in (*.zip) do (
    mkdir tmp
    cd tmp
    %ZIP7% x ..\%%z
    %ZIP7% a ..\%%z.re.zip -r -mx9 *
    cd ..
    rmdir /s /q tmp
)