-- phpMyAdmin SQL Dump
-- version 5.1.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 03, 2024 at 04:20 PM
-- Server version: 10.4.24-MariaDB
-- PHP Version: 8.1.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

--
-- Database: `barcode`
--

-- --------------------------------------------------------

--
-- Table structure for table `document`
--

CREATE TABLE `document` (
  `id` int(11) NOT NULL,
  `original_name` varchar(200) DEFAULT NULL,
  `nama_dokumen` varchar(200) NOT NULL,
  `nomor_dokumen` varchar(200) NOT NULL,
  `created_at` varchar(100) NOT NULL,
  `updated_at` varchar(100) NOT NULL,
  `ket` text DEFAULT NULL,
  `status` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `document`
--

INSERT INTO `document` (`id`, `original_name`, `nama_dokumen`, `nomor_dokumen`, `created_at`, `updated_at`, `ket`, `status`) VALUES
(5, 'TRANSKRIPttd.pdf', 'ijazah', '33333', '01-June-2024 23:59:31', '01-June-2024 23:59:31', 'Masukkan keterangan', 'approve'),
(6, 'TRANSKRIPttd.pdf', 'ijazah', '0000', '02-June-2024 01:07:23', '02-June-2024 01:09:55', 'Masukkan keterangan', 'approve'),
(7, 'TRANSKRIPttd.pdf', 'sertifikat', '11111', '03-June-2024 21:11:57', '03-June-2024 21:11:57', '-', 'pengajuan');

-- --------------------------------------------------------

--
-- Table structure for table `qrcode`
--

CREATE TABLE `qrcode` (
  `id` int(11) NOT NULL,
  `nama_qrcode` varchar(200) DEFAULT NULL,
  `original_name` varchar(200) DEFAULT NULL,
  `nomor_dokumen` varchar(200) NOT NULL,
  `nama_dokumen` varchar(200) DEFAULT NULL,
  `signature` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `qrcode`
--

INSERT INTO `qrcode` (`id`, `nama_qrcode`, `original_name`, `nomor_dokumen`, `nama_dokumen`, `signature`) VALUES
(5, 'hash4', NULL, '33333', 'sertifikat', NULL),
(6, 'hash10', NULL, '0000', 'sertifikat', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `nip` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `nip`, `password`, `role`) VALUES
(1, '123', '123', 'stTu'),
(2, '1234', '1234', 'kpBidang'),
(3, '12345', '12345', 'kpSekolah'),
(4, 'abib', 'abib', '1');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `document`
--
ALTER TABLE `document`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `qrcode`
--
ALTER TABLE `qrcode`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `document`
--
ALTER TABLE `document`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `qrcode`
--
ALTER TABLE `qrcode`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;
