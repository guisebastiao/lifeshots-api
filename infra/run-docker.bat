@echo off
echo Subindo containers com docker compose...
docker compose --env-file ../.env up -d
pause