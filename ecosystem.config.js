// PM2 process file for course-service (the same settings the GCP VMs use).
//
//   sudo mkdir -p /opt/learnhub /var/log/pm2
//   sudo cp target/course-service.jar /opt/learnhub/course-service.jar
//   pm2 start ecosystem.config.js --update-env      # start it (values come from the environment)
//   pm2 save                                        # remember the process list
//   pm2 startup systemd -u root --hp /root          # start PM2 again after a VM reboot
//
// autorestart: PM2 restarts the app if it crashes. Logs: /var/log/pm2/course-service-out.log and course-service-error.log

const clean = (obj) => Object.fromEntries(Object.entries(obj).filter(([, v]) => v !== undefined && v !== ''));

module.exports = {
  apps: [
    {
      name: 'course-service',
      script: 'java',
      args: '-Xms256m -Xmx512m -jar /opt/learnhub/course-service.jar',
      cwd: '/opt/learnhub',
      env: clean({
        SERVER_PORT: '8082',
        CONFIG_SERVER_URL: process.env.CONFIG_SERVER_URL,
        EUREKA_SERVER_URL: process.env.EUREKA_SERVER_URL,
        DB_URL: process.env.DB_URL,
        DB_USERNAME: process.env.DB_USERNAME,
        DB_PASSWORD: process.env.DB_PASSWORD,
      }),
      autorestart: true,
      max_restarts: 30,
      min_uptime: '20s',
      restart_delay: 5000,
      kill_timeout: 15000,
      merge_logs: true,
      log_date_format: 'YYYY-MM-DD HH:mm:ss',
      out_file: '/var/log/pm2/course-service-out.log',
      error_file: '/var/log/pm2/course-service-error.log',
    },
  ],
};
