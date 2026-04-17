const fs = require('fs');
const path = require('path');
const filePath = 'src/main/resources/static/dashboard.html';
let html = fs.readFileSync(filePath, 'utf8');

// 1. Remove the residue block (specific match to be safe)
const residue = `                                <h3 style="margin-bottom: 0.5rem;">\${post.title}</h3>
                                <p style="color: var(--text-muted);">\${post.content.substring(0, 150)}...</p>
                            </div >
                        </div >
            \`).join('');
                } else {
                    profilePostContainer.innerHTML = \`< p style = "text-align: center; color: var(--text-muted); padding: 2rem;" > \${ isSelf ? "You haven't" : "This user hasn't" } posted anything yet.</p > \`;
                }
            } catch (err) {
                console.error("Failed to load stats", err);
            }
        }`;

// We'll use a slightly more flexible approach for the residue if exact match fails
// But for now, let's try to fix the general issues first.

// Fix spaces inside \${ ... }
html = html.replace(/\$\{\s+([\s\S]+?)\s+\}/g, '${\$1}');

// Fix URLs with spaces
html = html.replace(/\/\s+\?/g, '/?');
html = html.replace(/\?\s+query\s+=\s+\$/g, '?query=$');
html = html.replace(/\/\s+status\s+\//g, '/status/');
html = html.replace(/\/\s+author\s+\//g, '/author/');
html = html.replace(/\?followerId\s+=\s+\$/g, '?followerId=$');

// Fix broken HTML tags
html = html.replace(/<\s+p\s+style\s+=\s+/g, '<p style=');
html = html.replace(/<\/\s+p\s+>/g, '</p>');
html = html.replace(/<\s+div\s+style\s+=\s+/g, '<div style=');
html = html.replace(/<\/\s+div\s+>/g, '</div>');

fs.writeFileSync(filePath, html);
console.log('General cleanup done');
