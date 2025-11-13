const fs = require('fs');
const path = require('path');

function loadEnvFile() {
  const envPath = path.join(__dirname, '..', 'ui.env');
  const env = {};

  if (fs.existsSync(envPath)) {
    const envFile = fs.readFileSync(envPath, 'utf8');
    envFile.split('\n').forEach(line => {
      const trimmedLine = line.trim();
      if (trimmedLine && !trimmedLine.startsWith('#')) {
        const [key, ...valueParts] = trimmedLine.split('=');
        if (key && valueParts.length > 0) {
          env[key.trim()] = valueParts.join('=').trim();
        }
      }
    });
  }

  return env;
}

function generateEnvironmentFiles() {
  const env = loadEnvFile();
  const googleMapsApiKey = env.GOOGLE_MAPS_API_KEY || '';

  const envDevContent = `export const environment = {
  production: false,
  googleMapsApiKey: '${googleMapsApiKey}'
};
`;

  const envProdContent = `export const environment = {
  production: true,
  googleMapsApiKey: '${googleMapsApiKey}'
};
`;

  const envDevPath = path.join(__dirname, '..', 'src', 'environments', 'environment.ts');
  const envProdPath = path.join(__dirname, '..', 'src', 'environments', 'environment.prod.ts');

  fs.writeFileSync(envDevPath, envDevContent, 'utf8');
  fs.writeFileSync(envProdPath, envProdContent, 'utf8');

  console.log('Environment files generated successfully');
  console.log(`Google Maps API Key: ${googleMapsApiKey ? '***' + googleMapsApiKey.slice(-4) : 'NOT SET'}`);
}

generateEnvironmentFiles();

