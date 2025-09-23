import { test, expect } from '@playwright/test';

test.describe('Self-HR Happy Path', () => {
  test('ホーム画面が正常に表示される', async ({ page }) => {
    await page.goto('/');

    await expect(page).toHaveTitle('Self-HR');

    const homeIconSvg = page.locator('svg:has(title:text("home icon"))');
    await expect(homeIconSvg).toBeVisible();

    const selfHrTitle = page.getByText('Self-HR');
    await expect(selfHrTitle).toBeVisible();

    const attendanceLink = page.getByRole('link', { name: 'Attendance' });
    await expect(attendanceLink).toBeVisible();

    const signInButton = page.getByRole('button', { name: 'SignIn' });
    await expect(signInButton).toBeVisible();

    const recordLink = page.getByRole('link', { name: 'record' });
    await expect(recordLink).toBeVisible();
  });

  test('サインインページへのナビゲーション', async ({ page }) => {
    await page.goto('/');

    const signInButton = page.getByRole('button', { name: 'SignIn' });
    await signInButton.click();

    await expect(page).toHaveURL('/sign-in');
  });

  test('ホームアイコンをクリックしてホームに戻る', async ({ page }) => {
    await page.goto('/');

    const homeLink = page.getByRole('link', { name: 'home icon Self-HR' });
    await homeLink.click();

    await expect(page).toHaveURL('/');
  });

  test('勤怠ページは認証が必要', async ({ page }) => {
    await page.goto('/');

    const attendanceLink = page.getByRole('link', { name: 'Attendance' });
    await attendanceLink.click();

    await expect(page).toHaveURL(/\/sign-in/);
    await expect(page.url()).toContain('path=%2Fattendance');
  });

  test('勤怠記録ページは認証が必要', async ({ page }) => {
    await page.goto('/');

    const recordLink = page.getByRole('link', { name: 'record' });
    await recordLink.click();

    await expect(page).toHaveURL(/\/sign-in/);
    await expect(page.url()).toContain('path=%2Fattendance%2Frecord');
  });

  test('TanStackルーターDevtoolsが表示される', async ({ page }) => {
    await page.goto('/');

    const devtoolsButton = page.getByRole('button', { name: 'Open TanStack Router Devtools' });
    await expect(devtoolsButton).toBeVisible();
  });
});