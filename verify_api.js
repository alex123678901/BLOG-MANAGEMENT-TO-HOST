const http = require('http');

async function request(options, body) {
    return new Promise((resolve, reject) => {
        const req = http.request(options, (res) => {
            let data = '';
            res.on('data', (chunk) => data += chunk);
            res.on('end', () => resolve({ statusCode: res.statusCode, body: data }));
        });
        req.on('error', (e) => reject(e));
        if (body) req.write(JSON.stringify(body));
        req.end();
    });
}

async function run() {
    try {
        const timestamp = Date.now();
        const username = `verify_${timestamp}`;
        const email = `verify_${timestamp}@example.com`;

        console.log(`Registering user: ${username}...`);
        const regRes = await request({
            hostname: 'localhost',
            port: 8080,
            path: '/api/users/register',
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        }, {
            username: username,
            password: 'password',
            email: email,
            firstName: 'Verify',
            lastName: 'Bot',
            bio: 'Automated verification user',
            roles: ['ROLE_READER']
        });

        console.log(`Registration Response: ${regRes.statusCode}`);
        if (regRes.statusCode !== 200 && regRes.statusCode !== 201) {
            console.error('Registration failed');
            return;
        }

        console.log(`Logging in...`);
        const loginRes = await request({
            hostname: 'localhost',
            port: 8080,
            path: '/api/auth/login',
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        }, { username: username, password: 'password' });

        console.log(`Login Response: ${loginRes.statusCode}`);
        const loginData = JSON.parse(loginRes.body);
        const token = loginData.accessToken;
        const userId = loginData.id;

        console.log(`Verifying Activity Log for Login...`);
        const activityRes = await request({
            hostname: 'localhost',
            port: 8080,
            path: `/api/activity/${userId}`,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        console.log(`Activity Log Response: ${activityRes.statusCode}`);
        const logs = JSON.parse(activityRes.body);
        console.log(`Logs found: ${logs.length}`);
        logs.forEach(log => console.log(`- ${log.action} at ${log.timestamp}`));

        const hasLoginLog = logs.some(l => l.action.includes('Logged in'));
        const hasRegLog = logs.some(l => l.action.includes('Registered'));

        if (hasLoginLog && hasRegLog) {
            console.log('VERIFICATION SUCCESS: Registration and Login logs detected.');
        } else {
            console.log('VERIFICATION FAILURE: Expected logs not found.');
        }

    } catch (e) {
        console.error(`Error: ${e.message}`);
    }
}

run();
