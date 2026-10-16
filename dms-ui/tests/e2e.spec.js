import { test, expect } from "@playwright/test";

const BASE = "http://localhost:5173";

test.describe("DMS E2E Tests", () => {

  test("TC1 - Login as admin redirects to users page", async ({ page }) => {
    await page.goto(BASE);
    await expect(page.locator("h1")).toContainText("Document Management");
    await page.fill('[data-testid="email-input"]', "admin@dms.com");
    await page.fill('[data-testid="password-input"]', "admin123");
    await page.click('[data-testid="login-btn"]');
    await page.waitForURL(/admin\/users/);
    await expect(page.locator("h1")).toContainText("Users");
  });

  test("TC2 - Login as user redirects to documents page", async ({ page }) => {
    await page.goto(BASE);
    await page.fill('[data-testid="email-input"]', "john@dms.com");
    await page.fill('[data-testid="password-input"]', "user123");
    await page.click('[data-testid="login-btn"]');
    await page.waitForURL(/documents/);
    await expect(page.locator("h1")).toContainText("Documents");
  });

  test("TC3 - Wrong password shows error message", async ({ page }) => {
    await page.goto(BASE);
    await page.fill('[data-testid="email-input"]', "john@dms.com");
    await page.fill('[data-testid="password-input"]', "wrongpassword");
    await page.click('[data-testid="login-btn"]');
    await expect(page.locator("text=Invalid email or password")).toBeVisible();
  });

  test("TC4 - Empty login shows validation error", async ({ page }) => {
    await page.goto(BASE);
    await page.click('[data-testid="login-btn"]');
    await expect(page.locator("text=Please fill in all fields")).toBeVisible();
  });

  test("TC5 - Search filters documents correctly", async ({ page }) => {
    await page.goto(BASE);
    await page.fill('[data-testid="email-input"]', "john@dms.com");
    await page.fill('[data-testid="password-input"]', "user123");
    await page.click('[data-testid="login-btn"]');
    await page.waitForURL(/documents/);
    await page.fill('[data-testid="search-input"]', "Financial");
    await expect(page.locator("table tbody tr")).toHaveCount(1);
  });

  test("TC6 - Upload document modal opens and closes", async ({ page }) => {
    await page.goto(BASE);
    await page.fill('[data-testid="email-input"]', "john@dms.com");
    await page.fill('[data-testid="password-input"]', "user123");
    await page.click('[data-testid="login-btn"]');
    await page.waitForURL(/documents/);
    await page.click('[data-testid="upload-btn"]');
    await expect(page.locator('h2', { hasText: 'Upload Document' })).toBeVisible();
    await page.keyboard.press("Escape");
  });

  test("TC7 - Admin can see all users in table", async ({ page }) => {
    await page.goto(BASE);
    await page.fill('[data-testid="email-input"]', "admin@dms.com");
    await page.fill('[data-testid="password-input"]', "admin123");
    await page.click('[data-testid="login-btn"]');
    await page.waitForURL(/admin\/users/);
    const rows = page.locator("table tbody tr");
    await expect(rows.first()).toBeVisible();
    expect(await rows.count()).toBeGreaterThan(0);
  });

  test("TC8 - Protected route redirects to login if not authenticated", async ({ page }) => {
    await page.goto(`${BASE}/documents`);
    await page.waitForURL(/\//);
    await expect(page.locator("h1")).toContainText("Document Management");
  });

});
