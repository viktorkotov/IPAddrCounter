# IPv4 Address Counter
Дан простой текстовый файл с IPv4 адресами. Одна строка – один адрес, примерно так:

145.67.23.4
8.34.5.23
89.54.3.124
89.54.3.124
3.45.71.5
...

Файл в размере не ограничен и может занимать десятки и сотни гигабайт.

Необходимо посчитать количество уникальных адресов в этом файле, затратив как можно меньше памяти и времени.

В реализации предполагается, что строки не пустые и там валидные IPv4 адреса. 

Файл для теста https://ecwid-vgv-storage.s3.eu-central-1.amazonaws.com/ip_addresses.zip. Внимание – файл весит около 20Gb, а распаковывается приблизительно в 120Gb.

## Запуск 
java ru.viktork.util.IPAddrCounter <путь к файлу>

Файл на входе должен быть в текстовом формате