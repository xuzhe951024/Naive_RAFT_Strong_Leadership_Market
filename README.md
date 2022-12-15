# How to Run

1. Adjust the config file (config.yml).

   ```yaml
   # configs of the market
   peerNumber: 6
   sellerNumber: 3
   buyerNumber: 3
   port: 2121
   deployOnSingleComputer: false
   sleepBeforeStart: 10000
   numberOfTests: 324
   
   
   maxmumStok: 100
   productNameList:
     - Fish
     - Salt
     - Boar
   
   
   
   # config of runner
   rpcBuffSize: 10240
   
   # not use any more
   neighbourNum: 1
   maxJump: 5
   ```



2. cd to the test dir.

   ```shell
   cd ${APPROOT}/testAuto
   ```

3. Run start script.

   ```shell
   sh runDocker.sh
   ```

4. Stop & Clean.

   ```shell
   docker-compose down & sh clear.sh
   ```
