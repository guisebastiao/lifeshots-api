echo "Subindo containers com docker compose..."
docker compose --env-file ../.env up -d

read -p "Pressione ENTER para continuar..."