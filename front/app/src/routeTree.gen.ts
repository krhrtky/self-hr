/* prettier-ignore-start */

/* eslint-disable */

// @ts-nocheck

// noinspection JSUnusedGlobalSymbols

// This file is auto-generated by TanStack Router

import { createFileRoute, lazyRouteComponent } from '@tanstack/react-router'

// Import Routes

import { Route as rootRoute } from './routes/__root'

// Create Virtual Routes

const SignInComponentImport = createFileRoute('/sign-in')()
const LayoutComponentImport = createFileRoute('/_layout')()
const LayoutIndexComponentImport = createFileRoute('/_layout/')()
const LayoutAttendanceIndexComponentImport = createFileRoute(
  '/_layout/attendance/',
)()
const LayoutAboutIndexComponentImport = createFileRoute('/_layout/about/')()
const LayoutAttendanceRecordComponentImport = createFileRoute(
  '/_layout/attendance/record',
)()
const LayoutAttendanceListComponentImport = createFileRoute(
  '/_layout/attendance/list',
)()

// Create/Update Routes

const SignInComponentRoute = SignInComponentImport.update({
  path: '/sign-in',
  getParentRoute: () => rootRoute,
} as any).update({
  component: lazyRouteComponent(
    () => import('./routes/sign-in.component'),
    'component',
  ),
})

const LayoutComponentRoute = LayoutComponentImport.update({
  id: '/_layout',
  getParentRoute: () => rootRoute,
} as any).update({
  component: lazyRouteComponent(
    () => import('./routes/_layout.component'),
    'component',
  ),
})

const LayoutIndexComponentRoute = LayoutIndexComponentImport.update({
  path: '/',
  getParentRoute: () => LayoutComponentRoute,
} as any).update({
  component: lazyRouteComponent(
    () => import('./routes/_layout/index.component'),
    'component',
  ),
})

const LayoutAttendanceIndexComponentRoute =
  LayoutAttendanceIndexComponentImport.update({
    path: '/attendance/',
    getParentRoute: () => LayoutComponentRoute,
  } as any).update({
    component: lazyRouteComponent(
      () => import('./routes/_layout/attendance/index.component'),
      'component',
    ),
  })

const LayoutAboutIndexComponentRoute = LayoutAboutIndexComponentImport.update({
  path: '/about/',
  getParentRoute: () => LayoutComponentRoute,
} as any).update({
  component: lazyRouteComponent(
    () => import('./routes/_layout/about/index.component'),
    'component',
  ),
})

const LayoutAttendanceRecordComponentRoute =
  LayoutAttendanceRecordComponentImport.update({
    path: '/attendance/record',
    getParentRoute: () => LayoutComponentRoute,
  } as any).update({
    component: lazyRouteComponent(
      () => import('./routes/_layout/attendance/record.component'),
      'component',
    ),
  })

const LayoutAttendanceListComponentRoute =
  LayoutAttendanceListComponentImport.update({
    path: '/attendance/list',
    getParentRoute: () => LayoutComponentRoute,
  } as any).update({
    component: lazyRouteComponent(
      () => import('./routes/_layout/attendance/list.component'),
      'component',
    ),
  })

// Populate the FileRoutesByPath interface

declare module '@tanstack/react-router' {
  interface FileRoutesByPath {
    '/_layout': {
      preLoaderRoute: typeof LayoutComponentImport
      parentRoute: typeof rootRoute
    }
    '/sign-in': {
      preLoaderRoute: typeof SignInComponentImport
      parentRoute: typeof rootRoute
    }
    '/_layout/': {
      preLoaderRoute: typeof LayoutIndexComponentImport
      parentRoute: typeof LayoutComponentImport
    }
    '/_layout/attendance/list': {
      preLoaderRoute: typeof LayoutAttendanceListComponentImport
      parentRoute: typeof LayoutComponentImport
    }
    '/_layout/attendance/record': {
      preLoaderRoute: typeof LayoutAttendanceRecordComponentImport
      parentRoute: typeof LayoutComponentImport
    }
    '/_layout/about/': {
      preLoaderRoute: typeof LayoutAboutIndexComponentImport
      parentRoute: typeof LayoutComponentImport
    }
    '/_layout/attendance/': {
      preLoaderRoute: typeof LayoutAttendanceIndexComponentImport
      parentRoute: typeof LayoutComponentImport
    }
  }
}

// Create and export the route tree

export const routeTree = rootRoute.addChildren([
  LayoutComponentRoute.addChildren([
    LayoutIndexComponentRoute,
    LayoutAttendanceListComponentRoute,
    LayoutAttendanceRecordComponentRoute,
    LayoutAboutIndexComponentRoute,
    LayoutAttendanceIndexComponentRoute,
  ]),
  SignInComponentRoute,
])

/* prettier-ignore-end */
