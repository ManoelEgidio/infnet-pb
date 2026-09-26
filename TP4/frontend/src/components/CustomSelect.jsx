import React, { useState, useRef, useEffect } from 'react';
import { ChevronDown, Check } from 'lucide-react';

export default function CustomSelect({
  value,
  onChange,
  options = [],
  placeholder = 'Selecione uma opção',
  icon: Icon = null,
  className = '',
  id = ''
}) {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  // Close when clicking outside
  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Close on Escape
  useEffect(() => {
    function handleKeyDown(event) {
      if (event.key === 'Escape') {
        setIsOpen(false);
      }
    }
    if (isOpen) {
      document.addEventListener('keydown', handleKeyDown);
      return () => document.removeEventListener('keydown', handleKeyDown);
    }
  }, [isOpen]);

  const selectedOption = options.find((opt) => String(opt.value) === String(value));

  const handleSelect = (val) => {
    onChange(val);
    setIsOpen(false);
  };

  return (
    <div className={`custom-select-wrapper ${className}`} ref={dropdownRef} id={id}>
      <button
        type="button"
        className={`custom-select-trigger ${isOpen ? 'active' : ''}`}
        onClick={() => setIsOpen(!isOpen)}
        aria-haspopup="listbox"
        aria-expanded={isOpen}
      >
        <div className="trigger-left">
          {Icon && <Icon size={16} className="trigger-icon" />}
          {selectedOption?.dotColor && (
            <span
              className="option-dot"
              style={{ backgroundColor: selectedOption.dotColor }}
            />
          )}
          <span className={`trigger-label ${!selectedOption ? 'placeholder' : ''}`}>
            {selectedOption ? selectedOption.label : placeholder}
          </span>
        </div>
        <ChevronDown
          size={16}
          className={`chevron-icon ${isOpen ? 'rotate-180' : ''}`}
        />
      </button>

      {isOpen && (
        <div className="custom-select-menu" role="listbox">
          {options.map((opt) => {
            const isSelected = String(opt.value) === String(value);
            return (
              <div
                key={opt.value}
                className={`custom-select-option ${isSelected ? 'selected' : ''}`}
                onClick={() => handleSelect(opt.value)}
                role="option"
                aria-selected={isSelected}
              >
                <div className="option-content">
                  {opt.dotColor && (
                    <span
                      className="option-dot"
                      style={{ backgroundColor: opt.dotColor }}
                    />
                  )}
                  {opt.badge && (
                    <span className={`badge-pill ${opt.badgeClass || ''}`}>
                      {opt.badge}
                    </span>
                  )}
                  <span className="option-text">{opt.label}</span>
                </div>
                {isSelected && <Check size={15} className="check-icon" />}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
