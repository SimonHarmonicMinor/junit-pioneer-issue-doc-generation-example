const fs = require('fs');

function renderIssues(issuesInfo) {
  issuesInfo.sort((issueLeft, issueRight) => {
    const parseIssueId = issue => Number.parseInt(issue.issueId.split("-")[1])
    return parseIssueId(issueRight) - parseIssueId(issueLeft);
  })
  return `
            <table>
                <tr>
                    <th>Issue</th>
                    <th>Test</th>
                </tr>
                ${issuesInfo.flatMap(issue => issue.tests.map(test => `
                    <tr>
                        <td>
                            <a target="_blank" href="https://hibernate.atlassian.net/browse/${issue.issueId}">${issue.issueId}</a>
                        </td>
                        <td>
                            <a target="_blank" href="https://github.com/SimonHarmonicMinor/junit-pioneer-issue-doc-generation-example/blob/master/src/test/java/${test.urlPath}">${test.testId}</a>
                        </td>
                    </tr>
                `)).join('')}
            </table>
        `
}

console.log(`
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <title>List of tests validation particular issues</title>
        <meta charset="UTF-8">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/yegor256/tacit@gh-pages/tacit-css-1.5.5.min.css"/>
    </head>
    <body>
        <h1>List of tests validation particular issues</h1>
        <h3>Click on issue ID to open it in separate tab. Click on test to open its declaration in separate tab.</h3>
        ${renderIssues(JSON.parse(fs.readFileSync('./build/classes/java/test/test-issues-info.json', 'utf8')))}
    </body>
    </html>
`)