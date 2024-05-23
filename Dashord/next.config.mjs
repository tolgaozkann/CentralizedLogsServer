/** @type {import('next').NextConfig} */
const nextConfig = {};

export default {
    async redirects() {
      return [
        {
          source: '/',
          destination: '/logList',
          permanent: true,
        },
      ];
    },
  };
