 java -cp app.jar -Dloader.main=com.zhexu.cs677_lab2.utils.ProfilesGenerator org.springframework.boot.loader.PropertiesLauncher config.yml
 echo ./cs677.lab*.peer*.com | xargs -n 1 cp app.jar
 echo ./cs677.lab*.peer*.com | xargs -n 1 cp wait-for.sh
 docker-compose up -d